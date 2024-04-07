package edu.ncssm.ftc.electricmayhem.units.base

import edu.ncssm.ftc.electricmayhem.units.derived.Derived

abstract class BaseUnit(val value: Double) {
    open val lengthPower = 0.0
    open val timePower = 0.0
    open val massPower = 0.0
    open val currentPower = 0.0
    open val temperaturePower = 0.0
    open val amountPower = 0.0

    operator fun times(other: BaseUnit): Derived {
        return Derived(value * other.value, listOf(this, other))
    }
    operator fun div(other: BaseUnit): Derived {
        return Derived(value / other.value, listOf(this), listOf(other))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseUnit) return false
        if (value != other.value) return false
        if (lengthPower != other.lengthPower) return false
        if (timePower != other.timePower) return false
        if (massPower != other.massPower) return false
        return true
    }
    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + lengthPower.hashCode()
        result = 31 * result + timePower.hashCode()
        result = 31 * result + massPower.hashCode()
        return result
    }
}