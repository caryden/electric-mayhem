package edu.ncssm.ftc.electricmayhem.units.derived

import edu.ncssm.ftc.electricmayhem.units.base.Length
import edu.ncssm.ftc.electricmayhem.units.base.Mass
import edu.ncssm.ftc.electricmayhem.units.base.Time

class Force(val newtons: Double)
    : Derived(newtons, listOf(Mass.Kilograms, Length.Meters), listOf(Time.Seconds, Time.Seconds)) {

    companion object {
        val Newtons = Force(1.0)
        val Pounds = Force(4.44822)
        val Ounces = Force(0.27801385)
        val Dynes = Force(1e-5)
        val PoundsForce = Force(4.44822)
        val Kiloponds = Force(9.80665)
        val Kips = Force(4448.22)
        val Meganewtons = Force(1e6)
        val Kilonewtons = Force(1e3)
        val Millinewtons = Force(1e-3)
        val Micronewtons = Force(1e-6)
        val Nanonewtons = Force(1e-9)
        val Piconewtons = Force(1e-12)
    }
    val pounds get() = newtons / Pounds.newtons
    val ounces get() = newtons / Ounces.newtons
    val dynes get() = newtons / Dynes.newtons
    val poundsForce get() = newtons / PoundsForce.newtons
    val kiloponds get() = newtons / Kiloponds.newtons
    val kips get() = newtons / Kips.newtons
    val meganewtons get() = newtons / Meganewtons.newtons
    val kilonewtons get() = newtons / Kilonewtons.newtons
    val millinewtons get() = newtons / Millinewtons.newtons
    val micronewtons get() = newtons / Micronewtons.newtons
    val nanonewtons get() = newtons / Nanonewtons.newtons
    val piconewtons get() = newtons / Piconewtons.newtons

    override operator fun plus(other: Derived): Force {
        return Force(super.plus(other).value)
    }
    override operator fun minus(other: Derived): Force {
        return Force(super.minus(other).value)
    }
    override operator fun times(scalar: Number): Force {
        return Force(newtons * scalar.toDouble())
    }
}