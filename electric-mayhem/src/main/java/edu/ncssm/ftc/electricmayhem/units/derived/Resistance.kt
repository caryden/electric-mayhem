package edu.ncssm.ftc.electricmayhem.units.derived

import edu.ncssm.ftc.electricmayhem.units.base.Current

class Resistance(val ohms : Double) : Derived(ohms, listOf(Voltage.Volts), listOf(Current.Amps)) {
    companion object {
        val Ohms = Resistance(1.0)
        val MilliOhms = Resistance(1e-3)
        val MicroOhms = Resistance(1e-6)
        val NanoOhms = Resistance(1e-9)
        val PicoOhms = Resistance(1e-12)
    }

    val milliOhms get() = ohms / MilliOhms.ohms
    val microOhms get() = ohms / MicroOhms.ohms
    val nanoOhms get() = ohms / NanoOhms.ohms
    val picoOhms get() = ohms / PicoOhms.ohms

    override operator fun plus(other: Derived): Resistance {
        return Resistance(super.plus(other).value)
    }
    override operator fun minus(other: Derived): Resistance {
        return Resistance(super.minus(other).value)
    }
    override operator fun times(scalar: Number): Resistance {
        return Resistance(ohms * scalar.toDouble())
    }
}