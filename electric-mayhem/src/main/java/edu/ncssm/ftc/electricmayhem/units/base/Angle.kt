package edu.ncssm.ftc.electricmayhem.units.base

class Angle(val radians: Double) :BaseUnit(radians) {
    companion object {
        val Radians = Angle(1.0)
        val Degrees = Angle(Math.PI / 180.0)
    }
    val degrees get() = radians / Degrees.radians

    operator fun plus(other: Angle): Angle {
        return Angle(radians + other.radians)
    }
    operator fun minus(other: Angle): Angle {
        return Angle(radians - other.radians)
    }
    operator fun times(scalar: Number): Angle {
        return Angle(radians * scalar.toDouble())
    }
}