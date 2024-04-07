package edu.ncssm.ftc.electricmayhem.units.base

class Length(val meters: Double) : BaseUnit(meters) {
    override val lengthPower = 1.0

    companion object {
        val Inches = Length(0.0254)
        val Feet = Length(0.3048)
        val Meters = Length(1.0)
        val Kilometers = Length(1000.0)
        val Miles = Length(1609.34)
        val Yards = Length(0.9144)
        val Centimeters = Length(0.01)
        val Millimeters = Length(0.001)
        val Micrometers = Length(1e-6)
        val Nanometers = Length(1e-9)
        val Angstroms = Length(1e-10)
        val LightYears = Length(9.461e15)
        val AstronomicalUnits = Length(1.496e11)
        val Parsecs = Length(3.086e16)
        val Furlongs = Length(201.168)
        val Chains = Length(20.1168)
        val Rods = Length(5.0292)
        val Fathoms = Length(1.8288)
        val Links = Length(0.201168)
        val Hands = Length(0.1016)
        val Paces = Length(0.762)
        val Fingers = Length(0.022225)
        val Barleycorns = Length(0.00846667)
        val Smoots = Length(1.7018)
        val NauticalMiles = Length(1852.0)
    }
    val inches get() = meters / Inches.meters
    val feet get() = meters / Feet.meters
    val kilometers get() = meters / Kilometers.meters
    val miles get() = meters / Miles.meters
    val yards get() = meters / Yards.meters
    val centimeters get() = meters / Centimeters.meters
    val millimeters get() = meters / Millimeters.meters
    val micrometers get() = meters / Micrometers.meters
    val nanometers get() = meters / Nanometers.meters
    val angstroms get() = meters / Angstroms.meters
    val lightYears get() = meters / LightYears.meters
    val astronomicalUnits get() = meters / AstronomicalUnits.meters
    val parsecs get() = meters / Parsecs.meters
    val furlongs get() = meters / Furlongs.meters
    val chains get() = meters / Chains.meters
    val rods get() = meters / Rods.meters
    val fathoms get() = meters / Fathoms.meters
    val links get() = meters / Links.meters
    val hands get() = meters / Hands.meters
    val paces get() = meters / Paces.meters
    val fingers get() = meters / Fingers.meters
    val barleycorns get() = meters / Barleycorns.meters
    val smoots get() = meters / Smoots.meters
    val nauticalMiles get() = meters / NauticalMiles.meters

    operator fun plus(other: Length): Length {
        return Length(this.meters + other.meters)
    }
    operator fun minus(other: Length): Length {
        return Length(this.meters - other.meters)
    }
    operator fun unaryMinus(): Length {
        return Length(-this.meters)
    }
    operator fun compareTo(other: Length): Int {
        return this.meters.compareTo(other.meters)
    }
    operator fun times(scalar: Number): Length {
        return Length(this.meters * scalar.toDouble())
    }
    operator fun div(scalar: Number): Length {
        return Length(this.meters / scalar.toDouble())
    }
}

