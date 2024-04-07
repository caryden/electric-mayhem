package edu.ncssm.ftc.electricmayhem.units.derived

class Voltage(val volts: Double) : Derived(volts, listOf(Energy.Joules), listOf(ElectricCharge.Coulombs)) {
    companion object {
        val Volts = Voltage(1.0)
        val MilliVolts = Voltage(1e-3)
        val MicroVolts = Voltage(1e-6)
        val NanoVolts = Voltage(1e-9)
        val PicoVolts = Voltage(1e-12)
    }

    val milliVolts get() = volts / MilliVolts.volts
    val microVolts get() = volts / MicroVolts.volts
    val nanoVolts get() = volts / NanoVolts.volts
    val picoVolts get() = volts / PicoVolts.volts

    override operator fun plus(other: Derived): Voltage {
        return Voltage(super.plus(other).value)
    }
    override operator fun minus(other: Derived): Voltage {
        return Voltage(super.minus(other).value)
    }
    override operator fun times(scalar: Number): Voltage {
        return Voltage(volts * scalar.toDouble())
    }
}