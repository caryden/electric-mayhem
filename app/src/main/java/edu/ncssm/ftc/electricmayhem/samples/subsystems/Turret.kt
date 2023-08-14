package edu.ncssm.ftc.electricmayhem.samples.subsystems

import actions.SubsystemAction
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.ServoImplEx
import edu.ncssm.ftc.electric_mayhem.motion.MotionProfileGenerator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import sensors.window
import subsystems.Subsystem
import kotlin.math.PI
import kotlin.math.abs

class Turret(private val motor : DcMotorEx) : Subsystem() {
    constructor(hardwareMap: HardwareMap, id: String) : this(hardwareMap.get(DcMotorEx::class.java, id))

    val arm = Arm()
    private val controlLoopTimeMs = 10L
    private var currentControlState = MutableStateFlow(TurretControlState(0.0, 0.0))
    private var targetControlState = MutableStateFlow(TurretControlState(0.0, 0.0))
    private var desiredMotorPower = MutableStateFlow(0.0)
    val current get() = currentControlState.asStateFlow()
    val target get() = targetControlState.asStateFlow()

    init {
        subsystems.add(arm)
    }

    override fun start() {
        super.start()
        startControlLoop()
        startMotorCommandLoop()
    }
    private fun startMotorCommandLoop() {
        // we collect the flow of the desired motor power here and set the motors to that power
        // if needed
        subsystemScope.launch {
            try {
                val tolerance = 0.01 // this is the tolerance for the motor power
                desiredMotorPower.window(2)
                    .filter {
                        val (previous, current) = it
                        abs(previous - current) > tolerance
                    }.map { it.last() }
                    .collect {
                        // here we actually send the command to the motor, but only if the power has changed meaningfully
                        motor.power = it
                    }
            } finally {
                // this block will run even if the coroutine is cancelled and return the motor to a safe state
                motor.power = 0.0
            }
        }
    }
    private fun startControlLoop() {

        // this launches the control loop coroutine
        subsystemScope.launch {
            try {
                while (isActive) {
                    // do some PID stuff here
                    val kP = 0.1
                    val error = targetControlState.value.angle - currentControlState.value.angle
                    desiredMotorPower.value = error * kP
                    delay(controlLoopTimeMs)
                }
            } finally {
                // This block will run even if the coroutine is cancelled.  Put any cleanup code here
            }
        }
    }

    private suspend fun waitForTargetAngleAchieved() {
        currentControlState.collect {
            if (it isWithinToleranceOf targetControlState.value)
                return@collect
        }
    }
    // these inner classes are the subsystem actions (commands), they have access to private vars of the edu.ncssm.ftc.electricmayhem.samples.subsystems.Turret (outer) class
    // they are SubsystemAction<edu.ncssm.ftc.electricmayhem.samples.subsystems.Turret> types so that these are the only ones that can mutate this subsystem.  As such,
    // these "require" this subsystem.  They primarily (almost exclusively) mute the targetControlState
    inner class MoveToAngle(private val desiredAngle: Double,) : SubsystemAction(this, {
        val target = TurretControlState(desiredAngle, 0.0)
        val profileTimeStepMs = 2 * controlLoopTimeMs // this seems right that this is 2x the control loop time (Nyquist)
        val motionProfileGenerator = MotionProfileGenerator(2.0 * PI, PI/2.0, PI/4.0, profileTimeStepMs)
        val profile = motionProfileGenerator.generateSCurveMotionProfile(currentControlState.value.asMotionState(), target.asMotionState())
        for(step in profile) {
            targetControlState.value = step.motionState.asTurretControlState()
            delay(profileTimeStepMs)
        }
        waitForTargetAngleAchieved()
    }){ }
}
