package edu.ncssm.ftc.electricmayhem.units.base

class Temperature(val kelvin: Double) : BaseUnit(kelvin) {
    override val temperaturePower = 1.0
    companion object {
        val Kelvin = Temperature(1.0)
        val Celsius = Temperature(1.0)
        val Fahrenheit = Temperature(1.0)
    }
    val celsius get() = kelvin - 273.15
    val fahrenheit get() = celsius * 9.0 / 5.0 + 32.0

    operator fun plus(other: Temperature): Temperature {
        return Temperature(kelvin + other.kelvin)
    }
    operator fun minus(other: Temperature): Temperature {
        return Temperature(kelvin - other.kelvin)
    }
    operator fun times(scalar: Number): Temperature {
        return Temperature(kelvin * scalar.toDouble())
    }
    operator fun div(scalar: Number): Temperature {
        return Temperature(kelvin / scalar.toDouble())
    }
    operator fun unaryMinus(): Temperature {
        return Temperature(-kelvin)
    }
    operator fun compareTo(other: Temperature): Int {
        return kelvin.compareTo(other.kelvin)
    }
}