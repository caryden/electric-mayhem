package edu.ncssm.ftc.electricmayhem.units.derived

import edu.ncssm.ftc.electricmayhem.units.base.Time

class Power(val watts: Double) : Derived(watts, listOf(Energy.Joules), listOf(Time.Seconds)) {
    companion object {
        val Watts = Power(1.0)
        val MilliWatts = Power(1e-3)
        val MicroWatts = Power(1e-6)
        val NanoWatts = Power(1e-9)
        val PicoWatts = Power(1e-12)
    }

    val milliWatts get() = watts / MilliWatts.watts
    val microWatts get() = watts / MicroWatts.watts
    val nanoWatts get() = watts / NanoWatts.watts
    val picoWatts get() = watts / PicoWatts.watts

    override operator fun plus(other: Derived): Power {
        return Power(super.plus(other).value)
    }
    override operator fun minus(other: Derived): Power {
        return Power(super.minus(other).value)
    }
    override operator fun times(scalar: Number): Power {
        return Power(watts * scalar.toDouble())
    }
}