package edu.ncssm.ftc.electricmayhem.devices.range.i2c.vl53l0x

import android.util.Log
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.I2cAddr
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.hardware.I2cDeviceSynch.ReadWindow
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice
import com.qualcomm.robotcore.hardware.I2cWaitControl
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.TypeConversion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.util.concurrent.atomic.AtomicInteger
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

// Lots of code in this class is based on the code from the following sources:
// https://www.artfulbytes.com/vl53l0x-post
// https://github.com/FIRST-Tech-Challenge/ftcrobotcontroller/wiki/Writing-an-I2C-Driver

@I2cDeviceType
@DeviceProperties(name = "VL53L0X ToF Laser Ranging Sensor", xmlTag = "VL53L0X")
class VL53L0X(deviceClient: I2cDeviceSynch?, deviceClientIsOwned: Boolean) :
    I2cDeviceSynchDevice<I2cDeviceSynch>(deviceClient, deviceClientIsOwned), Closeable {
    companion object {
        val VL53L0X_ADDRESS_DEFAULT : I2cAddr = I2cAddr.create7bit(0x29)

        val REG_IDENTIFICATION_MODEL_ID = 0xC0.toByte()
        val REG_VHV_CONFIG_PAD_SCL_SDA_EXTSUP_HV = 0x89.toByte()
        val REG_MSRC_CONFIG_CONTROL = 0x60.toByte()
        val REG_FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT = 0x44.toByte()
        val REG_SYSTEM_SEQUENCE_CONFIG = 0x01.toByte()
        val REG_DYNAMIC_SPAD_REF_EN_START_OFFSET = 0x4F.toByte()
        val REG_DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD = 0x4E.toByte()
        val REG_GLOBAL_CONFIG_REF_EN_START_SELECT = 0xB6.toByte()
        val REG_SYSTEM_INTERRUPT_CONFIG_GPIO = 0x0A.toByte()
        val REG_GPIO_HV_MUX_ACTIVE_HIGH = 0x84.toByte()
        val REG_SYSTEM_INTERRUPT_CLEAR = 0x0B.toByte()
        val REG_RESULT_INTERRUPT_STATUS = 0x13.toByte()
        val REG_SYSRANGE_START = 0x00.toByte()
        val REG_GLOBAL_CONFIG_SPAD_ENABLES_REF_0 = 0xB0.toByte()
        val REG_RESULT_RANGE_STATUS = 0x14.toByte()
        val REG_RESULT_RANGE_DISANCE_MM = (REG_RESULT_RANGE_STATUS + 10).toByte()
        val VL53L0X_EXPECTED_DEVICE_ID = 0xEE.toByte()
        val REG_SYSRANGE_MODE_BACKTOBACK = 0x02.toByte()
        val REG_SYSRANGE_MODE_SINGLESHOT = 0x01.toByte()

        val SPAD_TYPE_APERTURE = 0x01
        val SPAD_START_SELECT = 0xB4.toByte()
        val SPAD_MAX_COUNT = 44
        val SPAD_MAP_ROW_COUNT = 6
        val SPAD_ROW_SIZE = 8
        val SPAD_APERTURE_START_INDEX = 12
    }
    private var stopVariable = 0x00.toByte()
    private var hasDataInitBeenRun = false
    private var hasStaticInitBeenRun = false
    private var hasI2CCommunicationBeenValidated = false
    private var hasReferenceCalibrationBeenPerformed = false
    private var hasContinuousRangingBeenStarted = false
    private var currentRange = AtomicInteger(0)
    private var getRangeJob : Job? = null

    init {
        deviceClient!!.i2cAddress = VL53L0X_ADDRESS_DEFAULT

        // Once everything has been set up, we need to engage the sensor to start communicating.
        // We also need to run the arming state callback method that deals with situations involving USB cables disconnecting and reconnecting
        super.registerArmingStateCallback(false);

        // Sensor starts off disengaged so we can change things like I2C address. Need to engage
        this.deviceClient.engage();
    }
    override fun getManufacturer(): HardwareDevice.Manufacturer {
        return HardwareDevice.Manufacturer.Other
    }
    override fun getDeviceName(): String {
        return "VL53L0X Time-of-Flight (ToF) Laser Ranging Sensor"
    }
    override fun doInitialize(): Boolean {
        return if (validateI2CCommunication()) {
            Log.i("VL53L0X", "VL53L0X i2c communication validated successfully")
            dataInit()
            staticInit()
            performReferenceCalibration()
            setOptimalReadWindow()
            startContinuousRanging()
            true
        } else {
            false
        }
    }
     private fun setOptimalReadWindow() {
        // Sensor registers are read repeatedly and stored in a register. This method specifies the
        // registers and repeat read mode.
         val startRegister = REG_RESULT_INTERRUPT_STATUS.toInt()
         val endRegister = REG_RESULT_RANGE_DISANCE_MM.toInt() + 1 // +1 because the end register is a short
         val registerCount = endRegister - startRegister + 1 // +1 here to account for the fact that the end register is inclusive
        val readWindow = ReadWindow(startRegister, registerCount, I2cDeviceSynch.ReadMode.REPEAT)
        deviceClient.readWindow = readWindow
    }
    private fun validateI2CCommunication(): Boolean {
        if(hasI2CCommunicationBeenValidated) {
            return true
        } else {
            // Map of register addresses to expected values, these are from the datasheet
            // https://www.st.com/resource/en/datasheet/vl53l0x.pdf section 3.2 page 19

            val referenceRegisters8Bit = mapOf<Byte, Byte>(
                REG_IDENTIFICATION_MODEL_ID to VL53L0X_EXPECTED_DEVICE_ID,
                0xC1.toByte() to 0xAA.toByte(),
                0xC2.toByte() to 0x10.toByte()
            )

            // Validate 8-bit registers
            for ((address, expectedValue) in referenceRegisters8Bit) {
                val readValue = readByte(address)
                if (readValue != expectedValue) {
                    return false
                }
            }
            return true
        }
    }
    private fun dataInit() {
        // we only need to do this once per power cycle
        if(!hasDataInitBeenRun) {
            // Set 2v8 mode
            var vhvConfigSclSda = readByte(REG_VHV_CONFIG_PAD_SCL_SDA_EXTSUP_HV)

            vhvConfigSclSda = vhvConfigSclSda or 0x01
            writeByte(REG_VHV_CONFIG_PAD_SCL_SDA_EXTSUP_HV, vhvConfigSclSda)

            // Set I2C standard mode
            writeByte(0x88.toByte(), 0x00)

            writeByte(0x80.toByte(), 0x01)
            writeByte(0xFF.toByte(), 0x01)
            writeByte(0x00.toByte(), 0x00)
            stopVariable = readByte(0x91.toByte())
            writeByte(0x00.toByte(), 0x01)
            writeByte(0xFF.toByte(), 0x00)
            writeByte(0x80.toByte(), 0x00)

            hasDataInitBeenRun = true
            Log.i("VL53L0X", "VL53L0X data initialized successfully")

        }
    }
    private fun staticInit() {
        if(!hasStaticInitBeenRun) {
            setSpadsFromNvm()
            loadDefaultTuningSettings()
            configureInterrupt()
            setSequenceStepsEnabled(
                RangeSequenceStep.DSS,
                RangeSequenceStep.PRE_RANGE,
                RangeSequenceStep.FINAL_RANGE
            )
            hasStaticInitBeenRun = true
            Log.i("VL53L0X", "VL53L0X static initialized successfully")
        }
    }
    private fun loadDefaultTuningSettings() {
        val defaultTuningSettings = arrayOf<Pair<Byte, Byte>>(
            Pair(0xFF.toByte(), 0x01.toByte()),
            Pair(0x00.toByte(), 0x00.toByte()),
            Pair(0xFF.toByte(), 0x00.toByte()),
            Pair(0x09.toByte(), 0x00.toByte()),
            Pair(0x10.toByte(), 0x00.toByte()),
            Pair(0x11.toByte(), 0x00.toByte()),
            Pair(0x24.toByte(), 0x01.toByte()),
            Pair(0x25.toByte(), 0xFF.toByte()),
            Pair(0x75.toByte(), 0x00.toByte()),
            Pair(0xFF.toByte(), 0x01.toByte()),
            Pair(0x4E.toByte(), 0x2C.toByte()),
            Pair(0x48.toByte(), 0x00.toByte()),
            Pair(0x30.toByte(), 0x20.toByte()),
            Pair(0xFF.toByte(), 0x00.toByte()),
            Pair(0x30.toByte(), 0x09.toByte()),
            Pair(0x54.toByte(), 0x00.toByte()),
            Pair(0x31.toByte(), 0x04.toByte()),
            Pair(0x32.toByte(), 0x03.toByte()),
            Pair(0x40.toByte(), 0x83.toByte()),
            Pair(0x46.toByte(), 0x25.toByte()),
            Pair(0x60.toByte(), 0x00.toByte()),
            Pair(0x27.toByte(), 0x00.toByte()),
            Pair(0x50.toByte(), 0x06.toByte()),
            Pair(0x51.toByte(), 0x00.toByte()),
            Pair(0x52.toByte(), 0x96.toByte()),
            Pair(0x56.toByte(), 0x08.toByte()),
            Pair(0x57.toByte(), 0x30.toByte()),
            Pair(0x61.toByte(), 0x00.toByte()),
            Pair(0x62.toByte(), 0x00.toByte()),
            Pair(0x64.toByte(), 0x00.toByte()),
            Pair(0x65.toByte(), 0x00.toByte()),
            Pair(0x66.toByte(), 0xA0.toByte()),
            Pair(0xFF.toByte(), 0x01.toByte()),
            Pair(0x22.toByte(), 0x32.toByte()),
            Pair(0x47.toByte(), 0x14.toByte()),
            Pair(0x49.toByte(), 0xFF.toByte()),
            Pair(0x4A.toByte(), 0x00.toByte()),
            Pair(0xFF.toByte(), 0x00.toByte()),
            Pair(0x7A.toByte(), 0x0A.toByte()),
            Pair(0x7B.toByte(), 0x00.toByte()),
            Pair(0x78.toByte(), 0x21.toByte()),
            Pair(0xFF.toByte(), 0x01.toByte()),
            Pair(0x23.toByte(), 0x34.toByte()),
            Pair(0x42.toByte(), 0x00.toByte()),
            Pair(0x44.toByte(), 0xFF.toByte()),
            Pair(0x45.toByte(), 0x26.toByte()),
            Pair(0x46.toByte(), 0x05.toByte()),
            Pair(0x40.toByte(), 0x40.toByte()),
            Pair(0x0E.toByte(), 0x06.toByte()),
            Pair(0x20.toByte(), 0x1A.toByte()),
            Pair(0x43.toByte(), 0x40.toByte()),
            Pair(0xFF.toByte(), 0x00.toByte()),
            Pair(0x34.toByte(), 0x03.toByte()),
            Pair(0x35.toByte(), 0x44.toByte()),
            Pair(0xFF.toByte(), 0x01.toByte()),
            Pair(0x31.toByte(), 0x04.toByte()),
            Pair(0x4B.toByte(), 0x09.toByte()),
            Pair(0x4C.toByte(), 0x05.toByte()),
            Pair(0x4D.toByte(), 0x04.toByte()),
            Pair(0xFF.toByte(), 0x00.toByte()),
            Pair(0x44.toByte(), 0x00.toByte()),
            Pair(0x45.toByte(), 0x20.toByte()),
            Pair(0x47.toByte(), 0x08.toByte()),
            Pair(0x48.toByte(), 0x28.toByte()),
            Pair(0x67.toByte(), 0x00.toByte()),
            Pair(0x70.toByte(), 0x04.toByte()),
            Pair(0x71.toByte(), 0x01.toByte()),
            Pair(0x72.toByte(), 0xFE.toByte()),
            Pair(0x76.toByte(), 0x00.toByte()),
            Pair(0x77.toByte(), 0x00.toByte()),
            Pair(0xFF.toByte(), 0x01.toByte()),
            Pair(0x0D.toByte(), 0x01.toByte()),
            Pair(0xFF.toByte(), 0x00.toByte()),
            Pair(0x80.toByte(), 0x01.toByte()),
            Pair(0x01.toByte(), 0xF8.toByte()),
            Pair(0xFF.toByte(), 0x01.toByte()),
            Pair(0x8E.toByte(), 0x01.toByte()),
            Pair(0x00.toByte(), 0x01.toByte()),
            Pair(0xFF.toByte(), 0x00.toByte()),
            Pair(0x80.toByte(), 0x00.toByte())
        )
        for (pair in defaultTuningSettings) {
            writeByte(pair.first, pair.second)
        }
        Log.i("VL53L0X", "Finished loading default tunings")
    }
    private fun configureInterrupt() {
        // Interrupt on new sample ready
        writeByte(REG_SYSTEM_INTERRUPT_CONFIG_GPIO, 0x04.toByte())

        // Configure interrupt active low
        val gpio_hv_mux_active_high = readByte(REG_GPIO_HV_MUX_ACTIVE_HIGH)
        writeByte(REG_GPIO_HV_MUX_ACTIVE_HIGH, gpio_hv_mux_active_high and 0x10.toByte().inv())

        // Ensure Interrupts are cleared
        writeByte(REG_SYSTEM_INTERRUPT_CLEAR, 0x01.toByte())
        Log.i("VL53L0X", "Interrupt configured")
    }
    private fun setSequenceStepsEnabled(vararg sequenceSteps: RangeSequenceStep) {
        var sequenceConfig = 0
        for (sequenceStep in sequenceSteps) {
            sequenceConfig = sequenceConfig or sequenceStep.value
        }
        writeByte(REG_SYSTEM_SEQUENCE_CONFIG, sequenceConfig.toByte())
        Log.i("VL53L0X", "Sequence steps enabled")
    }
    private fun performSingleReferenceCalibration(calType : ReferenceCalibrationType) {
        when (calType) {
            ReferenceCalibrationType.CALIBRATION_TYPE_VHV -> {
                writeByte(REG_SYSTEM_SEQUENCE_CONFIG, 0x01.toByte())
                writeByte(REG_SYSRANGE_START, (0x01 or 0x40).toByte())
            }
            ReferenceCalibrationType.CALIBRATION_TYPE_PHASE -> {
                writeByte(REG_SYSTEM_SEQUENCE_CONFIG, 0x02.toByte())
                writeByte(REG_SYSRANGE_START, (0x01 or 0x00).toByte())
            }
        }

        // wait for interrupt
        var interruptStatus: Byte
        do {
            interruptStatus = readByte(REG_RESULT_INTERRUPT_STATUS)
        } while ((interruptStatus.toInt() and 0x07) == 0)

        // clear the interrupt and start a new range
        writeByte(REG_SYSTEM_INTERRUPT_CLEAR, 0x01.toByte())
        writeByte(REG_SYSRANGE_START, 0x00.toByte())
    }
    private fun performReferenceCalibration(forceRecalibration: Boolean = false) {
        // in theory, this should only be required if the temperature changes by more than 8 degrees C
        if(!hasReferenceCalibrationBeenPerformed or forceRecalibration) {

            performSingleReferenceCalibration(ReferenceCalibrationType.CALIBRATION_TYPE_VHV)
            Log.i("VL53L0X", "VHV Calibration performed")

            performSingleReferenceCalibration(ReferenceCalibrationType.CALIBRATION_TYPE_PHASE)
            Log.i("VL53L0X", "Phase Calibration performed")

            // restore the Sequence Steps
            setSequenceStepsEnabled(
                RangeSequenceStep.DSS,
                RangeSequenceStep.PRE_RANGE,
                RangeSequenceStep.FINAL_RANGE
            )
            hasReferenceCalibrationBeenPerformed = true
            Log.i("VL53L0X", "Sequence steps restored after calibration")
        }
    }
    private fun startContinuousRanging() {
        if(!hasContinuousRangingBeenStarted) {
            // Stop any ongoing range measuring
            // magic incantation from ST
            writeByte(0x80.toByte(), 0x01)
            writeByte(0xFF.toByte(), 0x01)
            writeByte(0x00.toByte(), 0x00)
            writeByte(0x91.toByte(), stopVariable)
            writeByte(0x00.toByte(), 0x01)
            writeByte(0xFF.toByte(), 0x00)
            writeByte(0x80.toByte(), 0x00)

            // Trigger a new range measurement
            writeByte(REG_SYSRANGE_START, REG_SYSRANGE_MODE_BACKTOBACK)

            // Wait for it to start
            var sysrangeStart: Byte
            do {
                sysrangeStart = readByte(REG_SYSRANGE_START)
            } while ((sysrangeStart.toInt() and 0x01) != 0)

            startRangingCoroutine()
            hasContinuousRangingBeenStarted = true
            Log.i("VL53L0X", "Continuous ranging started")
        }
    }
    public fun stop() {
        if (hasContinuousRangingBeenStarted)
            runBlocking {
                getRangeJob?.cancelAndJoin()

                // Stop any ongoing range measuring
                // magic incantation from ST
                writeByte(0x80.toByte(), 0x01)
                writeByte(0xFF.toByte(), 0x01)
                writeByte(0x00.toByte(), 0x00)
                writeByte(0x91.toByte(), stopVariable)
                writeByte(0x00.toByte(), 0x01)
                writeByte(0xFF.toByte(), 0x00)
                writeByte(0x80.toByte(), 0x00)

                hasContinuousRangingBeenStarted = false

            }
    }
    private fun startRangingCoroutine() {
        getRangeJob = CoroutineScope(Dispatchers.IO).launch {
            val measurementTime = ElapsedTime()
            while (this.isActive) {

                // Poll interrupt (wait to finish
                var interruptStatus: Byte
                do {
                    interruptStatus = readByte(REG_RESULT_INTERRUPT_STATUS)
                } while ((interruptStatus.toInt() and 0x07) == 0)

                // read range
                val range = readShort(REG_RESULT_RANGE_DISANCE_MM)

                // clear the interrupt
                writeByte(REG_SYSTEM_INTERRUPT_CLEAR, 0x01.toByte(), I2cWaitControl.ATOMIC)

                currentRange.set(range.toInt())

                val rangingIntervalMs = measurementTime.milliseconds()
                measurementTime.reset()

                delay(rangingIntervalMs.toLong() / 2) // sample at half the measurement interval (2x frequency)
            }
        }
    }
    override fun close() {
        this.stop()
    }
    fun getRange(): Int {
        return currentRange.get()
    }
    private fun readStrobe() {
        var strobe: Byte
        writeByte(0x83.toByte(), 0x00.toByte())
        do {
            strobe = readByte(0x83.toByte())
        } while (strobe == 0.toByte())
        writeByte(0x83.toByte(), 0x01.toByte())
    }
    private fun getSpadInfoFromNvm(): SpadInfo {
        writeByte(0x80.toByte(), 0x01.toByte())
        writeByte(0xFF.toByte(), 0x01.toByte())
        writeByte(0x00.toByte(), 0x00.toByte())
        writeByte(0xFF.toByte(), 0x06.toByte())

        var temporaryReg0x83 = readByte(0x83.toByte())
        writeByte(0x83.toByte(), (temporaryReg0x83 or 0x04).toByte())

        writeByte(0xFF.toByte(), 0x07.toByte())
        writeByte(0x81.toByte(), 0x01.toByte())
        writeByte(0x80.toByte(), 0x01.toByte())

        writeByte(0x94.toByte(), 0x6b.toByte())
        readStrobe()

        var temporaryReg0x90 = readShort(0x90.toByte()).toInt()
        val spadsToEnableCount = (temporaryReg0x90 shr 8) and 0x7f
        val spadType = (temporaryReg0x90 shr 15) and 0x01

        writeByte(0x81.toByte(), 0x00.toByte())
        writeByte(0xFF.toByte(), 0x06.toByte())

        temporaryReg0x83 = readByte(0x83.toByte())
        writeByte(0x83.toByte(), temporaryReg0x83 and 0xfb.toByte())

        writeByte(0xFF.toByte(), 0x01.toByte())
        writeByte(0x00.toByte(), 0x01.toByte())
        writeByte(0xFF.toByte(), 0x00.toByte())
        writeByte(0x80.toByte(), 0x00.toByte())

        val goodSpadMap = readBytes(REG_GLOBAL_CONFIG_SPAD_ENABLES_REF_0, SPAD_MAP_ROW_COUNT)

        return SpadInfo(spadsToEnableCount, spadType, goodSpadMap)
    }
    /*
        Sets the SPADs according to the meters saved to NVM by ST during production. Assuming
        similar conditions (e.g. no cover glass), this should give reasonable readings and we
        can avoid running ref spad management (tedious code).
    */
    private fun setSpadsFromNvm() {
        val spadInfo = getSpadInfoFromNvm()

        writeByte(0xFF.toByte(), 0x01.toByte())
        writeByte(REG_DYNAMIC_SPAD_REF_EN_START_OFFSET, 0x00.toByte())
        writeByte(REG_DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD, 0x2C.toByte())
        writeByte(0xFF.toByte(), 0x00.toByte())
        writeByte(REG_GLOBAL_CONFIG_REF_EN_START_SELECT, SPAD_START_SELECT)

        val offset = if (spadInfo.spadType == SPAD_TYPE_APERTURE) SPAD_APERTURE_START_INDEX else 0
        val spadMap = ByteArray(SPAD_MAP_ROW_COUNT)
        var spadsEnabledCount = 0

        for (row in 0 until SPAD_MAP_ROW_COUNT) {
            for (column in 0 until SPAD_ROW_SIZE) {
                val index = row * SPAD_ROW_SIZE + column
                if (index >= SPAD_MAX_COUNT) {
                    return
                }
                if (spadsEnabledCount == spadInfo.spadsToEnableCount) {
                    break
                }
                if (index < offset) {
                    continue
                }
                if (spadInfo.goodSpadMap[row].toInt() shr column and 0x01 > 0) {
                    spadMap[row] = (spadMap[row].toInt() or (0x01 shl column)).toByte()
                    spadsEnabledCount++
                }
            }
            if (spadsEnabledCount == spadInfo.spadsToEnableCount) {
                break
            }
        }

        if (spadsEnabledCount != spadInfo.spadsToEnableCount) {
            return
        }

        // Write the new SPAD configuration
        writeBytes(REG_GLOBAL_CONFIG_SPAD_ENABLES_REF_0, spadMap)

        Log.i("VL53L0X", "Set SPAD config from NVM ($spadsEnabledCount of ${spadInfo.spadsToEnableCount})")

    }

    private fun readByte(address: Byte): Byte {
        return deviceClient.read(address.toInt(), 1)[0]
    }
    private fun readBytes(address: Byte, count : Int): ByteArray {
        return deviceClient.read(address.toInt(), count)
    }

    private fun readShort(address: Byte): Short {
        return TypeConversion.byteArrayToShort(deviceClient.read(address.toInt(), 2))
    }
    private fun writeByte(address: Byte, value: Byte, waitControl: I2cWaitControl = I2cWaitControl.WRITTEN) {
        deviceClient.write(address.toInt(), byteArrayOf(value), waitControl)
    }
    private fun writeBytes(address: Byte, values: ByteArray, waitControl: I2cWaitControl = I2cWaitControl.WRITTEN) {
        deviceClient.write(address.toInt(), values, waitControl)
    }

}