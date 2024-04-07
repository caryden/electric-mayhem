package edu.ncssm.ftc.electricmayhem.units.derived

import edu.ncssm.ftc.electricmayhem.units.base.Length
import edu.ncssm.ftc.electricmayhem.units.base.Time

class Velocity(val meterPerSecond: Double) : Derived(meterPerSecond, listOf(Length.Meters, Time.Seconds)) {
    companion object {
        val MetersPerSecond = Velocity(1.0)
        val FeetPerSecond = Velocity(Length.Feet.meters)
        val KilometersPerHour = Velocity(Length.Kilometers.meters / Time.Hours.seconds)
        val MilesPerHour = Velocity(Length.Miles.meters / Time.Hours.seconds)
        val Knots = Velocity(Length.NauticalMiles.meters / Time.Hours.seconds)
        val Mach = Velocity(343.2)
        val SpeedOfLight = Velocity(299_792_458.0)
        val SpeedOfSound = Velocity(343.2)
        val SpeedOfSoundInWater = Velocity(1_484.0)
        val SpeedOfSoundInSteel = Velocity(5_960.0)
    }

    val feetPerSecond get() = meterPerSecond / FeetPerSecond.meterPerSecond
    val kilometersPerHour get() = meterPerSecond / KilometersPerHour.meterPerSecond
    val milesPerHour get() = meterPerSecond / MilesPerHour.meterPerSecond
    val knots get() = meterPerSecond / Knots.meterPerSecond
    val mach get() = meterPerSecond / Mach.meterPerSecond

    override operator fun plus(other: Derived): Velocity {
        return Velocity(super.plus(other).value)
    }
    override operator fun minus(other: Derived): Velocity {
        return Velocity(super.minus(other).value)
    }
    override operator fun times(scalar: Number): Velocity {
        return Velocity(this.meterPerSecond * scalar.toDouble())
    }
}