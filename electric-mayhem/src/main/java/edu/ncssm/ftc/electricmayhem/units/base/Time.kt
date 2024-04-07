package edu.ncssm.ftc.electricmayhem.units.base

class Time(val seconds: Double) : BaseUnit(seconds) {
    override val timePower = 1.0
    companion object {
        val Seconds = Time(1.0)
        val Minutes = Time(60.0)
        val Hours = Time(3600.0)
        val Days = Time(86400.0)
        val Weeks = Time(604800.0)
        val Years = Time(31536000.0)
        val Decades = Time(315360000.0)
        val Centuries = Time(3153600000.0)
        val Millenia = Time(31536000000.0)
        val Microseconds = Time(0.000001)
        val Milliseconds = Time(0.001)
        val Nanoseconds = Time(1e-9)
        val Picoseconds = Time(1e-12)
        val Femtoseconds = Time(1e-15)
        val Attoseconds = Time(1e-18)
        val Zeptoseconds = Time(1e-21)
        val Yoctoseconds = Time(1e-24)
    }
    val minutes get() = seconds / Minutes.seconds
    val hours get() = seconds / Hours.seconds
    val days get() = seconds / Days.seconds
    val weeks get() = seconds / Weeks.seconds
    val years get() = seconds / Years.seconds
    val decades get() = seconds / Decades.seconds
    val centuries get() = seconds / Centuries.seconds
    val millenia get() = seconds / Millenia.seconds
    val microseconds get() = seconds / Microseconds.seconds
    val milliseconds get() = seconds / Milliseconds.seconds
    val nanoseconds get() = seconds / Nanoseconds.seconds
    val picoseconds get() = seconds / Picoseconds.seconds
    val femtoseconds get() = seconds / Femtoseconds.seconds
    val attoseconds get() = seconds / Attoseconds.seconds
    val zeptoseconds get() = seconds / Zeptoseconds.seconds
    val yoctoseconds get() = seconds / Yoctoseconds.seconds



    operator fun plus(other: Time): Time {
        return Time(seconds + other.seconds)
    }
    operator fun minus(other: Time): Time {
        return Time(seconds - other.seconds)
    }
    operator fun times(scalar: Number): Time {
        return Time(seconds * scalar.toDouble())
    }
    operator fun div(scalar: Double): Time {
        return Time(seconds / scalar)
    }
    operator fun div(other: Time): Double {
        return seconds / other.seconds
    }
    operator fun unaryMinus(): Time {
        return Time(-seconds)
    }
    operator fun compareTo(other: Time): Int {
        return seconds.compareTo(other.seconds)
    }

    override fun toString(): String {
        return "$seconds s"
    }
}