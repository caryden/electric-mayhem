package edu.ncssm.ftc.electricmayhem.samples.subsystems

import edu.ncssm.ftc.electric_mayhem.core.motion.data.MotionState
import kotlin.math.abs


data class TurretControlState(val angle: Double, val angularVelocity: Double, val acceleration: Double = 0.0, val jerk: Double = 0.0) {

    infix fun isWithinToleranceOf(other: TurretControlState): Boolean {
        val angleTolerance = 0.1
        val angularVelocityTolerance = 0.1
        val accelerationTolerance = 0.1
        val jerkTolerance = 0.1
        return abs(angle - other.angle) < angleTolerance &&
                    abs(angularVelocity - other.angularVelocity) < angularVelocityTolerance &&
                    abs(acceleration - other.acceleration) < accelerationTolerance &&
                    abs(jerk - other.jerk) < jerkTolerance
    }
    fun asMotionState(): MotionState {
        return MotionState(angle, angularVelocity, acceleration, jerk)
    }
}
fun MotionState.asTurretControlState(): TurretControlState {
    return TurretControlState(x, v, a, j)
}
