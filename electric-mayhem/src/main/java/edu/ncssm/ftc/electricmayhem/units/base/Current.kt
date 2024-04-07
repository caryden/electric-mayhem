package edu.ncssm.ftc.electricmayhem.units.base

class Current(val amps : Double) : BaseUnit(amps) {
    override val currentPower = 1.0

    companion object {
        val Amps = Current(1.0)
        val Milliamps = Current(0.001)
        val Microamps = Current(0.000001)
        val Nanoamps = Current(1e-9)
        val Picoamps = Current(1e-12)
        val Femtoamps = Current(1e-15)
        val Attoamps = Current(1e-18)
        val Zeptoamps = Current(1e-21)
        val Yoctoamps = Current(1e-24)
        val Kiloamps = Current(1e3)
        val Megaamps = Current(1e6)
        val Gigaamps = Current(1e9)
        val Teraamps = Current(1e12)
    }

    val milliamps get() = amps / Milliamps.amps
    val microamps get() = amps / Microamps.amps
    val nanoamps get() = amps / Nanoamps.amps
    val picoamps get() = amps / Picoamps.amps
    val femtoamps get() = amps / Femtoamps.amps
    val attoamps get() = amps / Attoamps.amps
    val zeptoamps get() = amps / Zeptoamps.amps
    val yoctoamps get() = amps / Yoctoamps.amps
    val kiloamps get() = amps / Kiloamps.amps
    val megaamps get() = amps / Megaamps.amps
    val gigaamps get() = amps / Gigaamps.amps
    val teraamps get() = amps / Teraamps.amps

    operator fun plus(other: Current): Current {
        return Current(amps + other.amps)
    }
    operator fun minus(other: Current): Current {
        return Current(amps - other.amps)
    }
    operator fun times(scalar: Number): Current {
        return Current(amps * scalar.toDouble())
    }
    operator fun div(scalar: Number): Current {
        return Current(amps / scalar.toDouble())
    }
    operator fun unaryMinus(): Current {
        return Current(-amps)
    }
    operator fun compareTo(other: Current): Int {
        return amps.compareTo(other.amps)
    }





}