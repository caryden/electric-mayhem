package edu.ncssm.ftc.electricmayhem.units.base

class Mass(val kilograms: Double) : BaseUnit(kilograms) {
    override val massPower = 1.0

    companion object {
        val Grams get() = Mass(0.001)
        val Kilograms get() = Mass(1.0)
        val Pounds get() = Mass(0.453592)
        val Ounces get() = Mass(0.0283495)
        val Tons get() = Mass(907.185)
        val MetricTons get() = Mass(1000.0)
        val Slugs get() = Mass(14.5939)
        val Grains get() = Mass(0.000064798)
        val Carats get() = Mass(0.0002)
        val TroyOunces get() = Mass(0.0311035)
        val TroyPounds get() = Mass(0.373242)
        val Pennyweights get() = Mass(0.00155517)
        val Scruples get() = Mass(0.00129598)
        val Drachms get() = Mass(0.00388793)
        val Stone get() = Mass(6.35029)
        val Quarters get() = Mass(12.7006)
        val Hundredweights get() = Mass(50.8023)
        val LongTons get() = Mass(1016.05)
        val Milligrams get() = Mass(0.000001)
        val Micrograms get() = Mass(1e-9)
        val Nanograms get() = Mass(1e-12)
        val Picograms get() = Mass(1e-15)
        val Femtograms get() = Mass(1e-18)
        val Attograms get() = Mass(1e-21)
        val Zeptograms get() = Mass(1e-24)
        val Megagrams get() = Mass(1000.0)
        val Gigagrams get() = Mass(1e6)
        val Teragrams get() = Mass(1e9)
    }

    val grams get() = kilograms / Grams.kilograms
    val pounds get() = kilograms / Pounds.kilograms
    val ounces get() = kilograms / Ounces.kilograms
    val tons get() = kilograms / Tons.kilograms
    val metricTons get() = kilograms / MetricTons.kilograms
    val slugs get() = kilograms / Slugs.kilograms
    val grains get() = kilograms / Grains.kilograms
    val carats get() = kilograms / Carats.kilograms
    val troyOunces get() = kilograms / TroyOunces.kilograms
    val troyPounds get() = kilograms / TroyPounds.kilograms
    val pennyweights get() = kilograms / Pennyweights.kilograms
    val scruples get() = kilograms / Scruples.kilograms
    val drachms get() = kilograms / Drachms.kilograms
    val stone get() = kilograms / Stone.kilograms
    val quarters get() = kilograms / Quarters.kilograms
    val hundredweights get() = kilograms / Hundredweights.kilograms
    val longTons get() = kilograms / LongTons.kilograms
    val milligrams get() = kilograms / Milligrams.kilograms
    val micrograms get() = kilograms / Micrograms.kilograms
    val nanograms get() = kilograms / Nanograms.kilograms
    val picograms get() = kilograms / Picograms.kilograms
    val femtograms get() = kilograms / Femtograms.kilograms
    val attograms get() = kilograms / Attograms.kilograms
    val zeptograms get() = kilograms / Zeptograms.kilograms
    val megagrams get() = kilograms / Megagrams.kilograms
    val gigagrams get() = kilograms / Gigagrams.kilograms
    val teragrams get() = kilograms / Teragrams.kilograms


    operator fun times(scalar: Number): Mass {
        return Mass(kilograms * scalar.toDouble())
    }
    operator fun div(scalar: Double): Mass {
        return Mass(kilograms / scalar)
    }
    operator fun unaryMinus(): Mass {
        return Mass(-kilograms)
    }
    operator fun plus(other: Mass): Mass {
        return Mass(kilograms + other.kilograms)
    }
    operator fun minus(other: Mass): Mass {
        return Mass(kilograms - other.kilograms)
    }
}

