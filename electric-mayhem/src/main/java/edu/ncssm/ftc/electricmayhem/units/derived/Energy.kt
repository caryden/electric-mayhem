package edu.ncssm.ftc.electricmayhem.units.derived

import edu.ncssm.ftc.electricmayhem.units.base.Length
import edu.ncssm.ftc.electricmayhem.units.base.Mass
import edu.ncssm.ftc.electricmayhem.units.base.Time

class Energy(val joules: Double) : Derived(joules, listOf(Mass.Kilograms, Length.Meters, Length.Meters), listOf(Time.Seconds, Time.Seconds)) {

    companion object {
        val Joules = Energy(1.0)
        val ElectronVolts = Energy(1.602_176_634e-19)
        val Calories = Energy(4.184)
        val BTUs = Energy(1_055.055_852_62)
        val FootPounds = Energy(1.355_817_948_331_4004)
        val WattHours = Energy(3_600.0)
        val KilowattHours = Energy(3_600_000.0)
        val MegawattHours = Energy(3_600_000_000.0)
        val GigawattHours = Energy(3_600_000_000_000.0)
        val TerawattHours = Energy(3_600_000_000_000_000.0)
        val PetawattHours = Energy(3_600_000_000_000_000_000.0)
        val ExawattHours = Energy(3_600_000_000_000_000_000_000.0)
        val ZettawattHours = Energy(3_600_000_000_000_000_000_000_000.0)
        val YottawattHours = Energy(3_600_000_000_000_000_000_000_000_000.0)
    }

    val electronVolts get() = joules / ElectronVolts.joules
    val calories get() = joules / Calories.joules
    val btus get() = joules / BTUs.joules
    val footPounds get() = joules / FootPounds.joules
    val wattHours get() = joules / WattHours.joules
    val kilowattHours get() = joules / KilowattHours.joules
    val megawattHours get() = joules / MegawattHours.joules
    val gigawattHours get() = joules / GigawattHours.joules
    val terawattHours get() = joules / TerawattHours.joules
    val petawattHours get() = joules / PetawattHours.joules
    val exawattHours get() = joules / ExawattHours.joules
    val zettawattHours get() = joules / ZettawattHours.joules
    val yottawattHours get() = joules / YottawattHours.joules

    override operator fun plus(other: Derived): Energy {
        return Energy(super.plus(other).value)
    }
    override operator fun minus(other: Derived): Energy {
        return Energy(super.minus(other).value)
    }
    override operator fun times(scalar: Number): Energy {
        return Energy(joules * scalar.toDouble())
    }
}