package edu.ncssm.ftc.electricmayhem.units.derived

import edu.ncssm.ftc.electricmayhem.units.base.BaseUnit

open class Derived(value : Double,
                   private val numerator : List<BaseUnit>,
                   private val denominator : List<BaseUnit> = mutableListOf()) : BaseUnit(value) {

    override val lengthPower = numerator.sumOf { it.lengthPower } - denominator.sumOf { it.lengthPower }
    override val timePower = numerator.sumOf { it.timePower } - denominator.sumOf { it.timePower }
    override val massPower = numerator.sumOf { it.massPower } - denominator.sumOf { it.massPower }
    operator fun times(other: Derived): Derived {
        val newNumerator = mutableListOf<BaseUnit>()
        val newDenominator = mutableListOf<BaseUnit>()
        newNumerator.addAll(numerator)
        newNumerator.addAll(other.numerator)
        newDenominator.addAll(denominator)
        newDenominator.addAll(other.denominator)
        return Derived(value * other.value, newNumerator, newDenominator)
    }
    operator fun div(other: Derived): Derived {
        val newNumerator = mutableListOf<BaseUnit>()
        val newDenominator = mutableListOf<BaseUnit>()
        newNumerator.addAll(numerator)
        newDenominator.addAll(denominator)
        newNumerator.addAll(other.denominator)
        newDenominator.addAll(other.numerator)
        return Derived(value / other.value, newNumerator, newDenominator)
    }
    operator fun unaryMinus(): Derived {
        return Derived(-value, numerator, denominator)
    }
    infix fun hasSameUnits(other: Derived): Boolean {
        return lengthPower == other.lengthPower &&
                timePower == other.timePower &&
                massPower == other.massPower
    }
    open operator fun plus(other: Derived): Derived {
        if (!hasSameUnits(other))
            throw IllegalArgumentException("Units do not match")
        return Derived(value + other.value, numerator, denominator)
    }
    open operator fun minus(other: Derived): Derived {
        if (!hasSameUnits(other))
            throw IllegalArgumentException("Units do not match")
        return Derived(value - other.value, numerator, denominator)
    }
    open operator fun times(scalar: Number): Derived {
        return Derived(value * scalar.toDouble(), numerator, denominator)
    }
    operator fun div(scalar: Number): Derived {
        return Derived(value / scalar.toDouble(), numerator, denominator)
    }

  }