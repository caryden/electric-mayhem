package edu.ncssm.ftc.electricmayhem.core.subsystems

import com.qualcomm.robotcore.hardware.DcMotorEx
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import sensors.SensorFlow
import subsystems.Subsystem
import kotlin.math.abs

class DcMotor(private val motor : DcMotorEx, defaultPower : Double = 0.0, powerTolerance : Double = 0.01, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : Subsystem() {
    private val stateFlow = MutableStateFlow(defaultPower)
    private val motorScope = CoroutineScope(dispatcher)
    val encoder = SensorFlow({ motor.currentPosition }, 10L, motorScope)
    var power : Double
        get() = stateFlow.value
        set(value) { stateFlow.value = value }
    init {
        motorScope.launch {
            var lastValueWritten = defaultPower
            try {
                stateFlow
                    .filter { abs(it - lastValueWritten) > powerTolerance }
                    .collectLatest {
                        // here we actually send the command to the motor, but only if the power has changed meaningfully (see the filter above)
                        // also note that we use collectLatest so that if the power changes again before the previous command has been sent, we cancel the previous command
                        motor.power = it
                        lastValueWritten = it
                    }
            } finally {
                // this block will run even if the coroutine is cancelled and return the actuator to a safe state
                motor.power = defaultPower
            }
        }
    }
    override fun close() {
        if(motorScope.isActive)
            motorScope.cancel()
    }
}