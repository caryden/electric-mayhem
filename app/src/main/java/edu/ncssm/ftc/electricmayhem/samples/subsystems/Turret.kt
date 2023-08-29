package edu.ncssm.ftc.electricmayhem.samples.subsystems

import edu.ncssm.ftc.electricmayhem.core.subsystems.SubsystemCommand
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import edu.ncssm.ftc.electricmayhem.core.motion.MotionProfileGenerator
import edu.ncssm.ftc.electricmayhem.core.subsystems.DcMotor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import subsystems.Subsystem
import kotlin.math.PI

class Turret(private val motor : DcMotorEx) : Subsystem() {
    constructor(hardwareMap: HardwareMap, id: String) : this(hardwareMap.get(DcMotorEx::class.java, id))

    val arm = Arm()
    private val controlLoopTimeMs = 10L
    private val currentControlState = MutableStateFlow(TurretControlState(0.0, 0.0))
    private val targetControlState = MutableStateFlow(TurretControlState(0.0, 0.0))
    private val turretMotor =DcMotor(motor, 0.0, 0.01)
    val current get() = currentControlState.asStateFlow()
    val target get() = targetControlState.asStateFlow()
    init {
        subsystems.add(arm)
    }
    override fun start() {
        super.start()
        startControlLoop()
    }
    private fun startControlLoop() {
        // this launches the control loop coroutine
        subsystemScope.launch {
            try {
                while (isActive) {
                    // do some PID stuff here
                    val kP = 0.1
                    val error = targetControlState.value.angle - currentControlState.value.angle
                    turretMotor.power = error * kP
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
    inner class MoveToAngle(private val desiredAngle: Double,) : SubsystemCommand(this, {
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
