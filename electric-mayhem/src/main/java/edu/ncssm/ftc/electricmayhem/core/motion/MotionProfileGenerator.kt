package edu.ncssm.ftc.electricmayhem.core.motion

import edu.ncssm.ftc.electricmayhem.core.motion.data.MotionProfileStep
import edu.ncssm.ftc.electricmayhem.core.motion.data.MotionState
import edu.ncssm.ftc.electricmayhem.core.util.epsilonEquals
import kotlin.math.abs
import kotlin.math.sign

class MotionProfileGenerator(val maxVelocity: Double, val maxAcceleration: Double, val maxJerk: Double, val timeStepMs : Long) {
    init {
        if (maxVelocity <= 0.0) throw IllegalArgumentException("Max velocity must be positive")
        if (maxAcceleration <= 0.0) throw IllegalArgumentException("Max acceleration must be positive")
        if (maxJerk <= 0.0) throw IllegalArgumentException("Max jerk must be positive")
        if (timeStepMs <= 0) throw IllegalArgumentException("Time step must be positive")
        if (maxJerk epsilonEquals 0.0) throw IllegalArgumentException("Max jerk must be non-zero")
        if (maxAcceleration epsilonEquals 0.0) throw IllegalArgumentException("Max acceleration must be non-zero")
    }
    fun generateSCurveMotionProfile(currentState: MotionState, targetState: MotionState, tolerance : Double = 1e-6): List<MotionProfileStep> {
        val motionProfileSteps = mutableListOf<MotionProfileStep>()
        val timeStep = timeStepMs / 1000.0
        var t = 0.0
        var x = currentState.x
        var v = currentState.v
        var a = 0.0
        var j = 0.0
        val direction = sign(targetState.x - currentState.x)

        // Define the different phases of motion
        val jerkTime = maxAcceleration / maxJerk
        val accelTime = maxVelocity / maxAcceleration
        val totalAccelTime = 2.0 * jerkTime + accelTime
        val coastTime = (targetState.x - currentState.x) / maxVelocity - totalAccelTime

        var phase = 1

        while (abs(x - targetState.x) > tolerance) {
            when (phase) {
                1 -> { // Ramp up jerk
                    j = maxJerk * direction
                    if (t >= jerkTime) phase++
                }
                2 -> { // Constant acceleration
                    j = 0.0
                    if (t >= jerkTime + accelTime) phase++
                }
                3 -> { // Ramp down jerk
                    j = -maxJerk * direction
                    if (t >= totalAccelTime) phase++
                }
                4 -> { // Constant velocity
                    j = 0.0
                    a = 0.0
                    if (t >= totalAccelTime + coastTime) phase++
                }
                5 -> { // Ramp up jerk (deceleration)
                    j = -maxJerk * direction
                    if (t >= totalAccelTime + coastTime + jerkTime) phase++
                }
                6 -> { // Constant deceleration
                    j = 0.0
                    if (t >= totalAccelTime + coastTime + totalAccelTime - accelTime) phase++
                }
                7 -> { // Ramp down jerk (deceleration)
                    j = maxJerk * direction
                    if (t >= totalAccelTime + coastTime + totalAccelTime) phase++
                }
            }
            a += j * timeStep
            v += a * timeStep
            x += v * timeStep
            motionProfileSteps.add(MotionProfileStep(t.toLong(), MotionState(x, v, a, j)))
            t += timeStep
        }
        return motionProfileSteps
    }
}


