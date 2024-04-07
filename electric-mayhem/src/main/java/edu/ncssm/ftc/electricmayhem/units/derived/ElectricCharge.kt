package edu.ncssm.ftc.electricmayhem.units.derived

import edu.ncssm.ftc.electricmayhem.units.base.Current
import edu.ncssm.ftc.electricmayhem.units.base.Time

class ElectricCharge(val coulombs: Double) : Derived(coulombs, listOf(Current.Amps, Time.Seconds)) {
    companion object {
        val Coulombs = ElectricCharge(1.0)
        val MilliCoulombs = ElectricCharge(1e-3)
        val MicroCoulombs = ElectricCharge(1e-6)
        val NanoCoulombs = ElectricCharge(1e-9)
        val PicoCoulombs = ElectricCharge(1e-12)
    }

    val milliCoulombs get() = coulombs / MilliCoulombs.coulombs
    val microCoulombs get() = coulombs / MicroCoulombs.coulombs
    val nanoCoulombs get() = coulombs / NanoCoulombs.coulombs
    val picoCoulombs get() = coulombs / PicoCoulombs.coulombs

    override operator fun plus(other: Derived): ElectricCharge {
        return ElectricCharge(super.plus(other).value)
    }
    override operator fun minus(other: Derived): ElectricCharge {
        return ElectricCharge(super.minus(other).value)
    }
    override operator fun times(scalar: Number): ElectricCharge {
        return ElectricCharge(coulombs * scalar.toDouble())
    }

}