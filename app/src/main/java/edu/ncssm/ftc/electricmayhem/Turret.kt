package edu.ncssm.ftc.electricmayhem

import actions.SubsystemAction
import kotlinx.coroutines.*
import subsystems.Subsystem
import java.util.concurrent.atomic.AtomicReference


class Turret : Subsystem() {
    val arm = Arm()

    private var currentAngleAtomic = AtomicReference(0.0)
    private var targetAngleAtomic = AtomicReference(0.0)
    var currentAngle : Double
        get() = currentAngleAtomic.get()
        set(value) = currentAngleAtomic.set(value)
    var targetAngle : Double
        get() = targetAngleAtomic.get()
        set(value) = targetAngleAtomic.set(value)

    init {
        subsystems.add(arm)
        startControlLoop()
    }

    private fun startControlLoop() {
        subsystemScope.launch {
            try {
                val loopTimeMs = 10L
                while (isActive) {
                    // do some PID stuff here

                    delay(loopTimeMs)
                }
            } finally {
                // This block will run even if the coroutine is cancelled.
                // Put your motor clean-up code here to return everything to a safe mode
                setMotorsToZeroPower()
            }
        }
    }
    private fun setMotorsToZeroPower() {
        // Your code to set motors to zero power
    }

    // these inner classes are the subsystem actions (commands), they have access to private vars of the edu.ncssm.ftc.electricmayhem.Turret (outer) class
    // they are SubsystemAction<edu.ncssm.ftc.electricmayhem.Turret> types so that these are the only ones that can mutate this subsystem.  As such,
    // these "require" this subsystem.
    inner class MoveToAngle(private val desiredAngle: Double,) : SubsystemAction(this, {
            targetAngle = desiredAngle
        }){
    }
}
