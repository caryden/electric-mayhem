package edu.ncssm.ftc.electricmayhem.units.base


class Amount(val amount: Double) : BaseUnit(amount) {
    override val amountPower = 1.0
    companion object {
        val Mole = Amount(6.02214076e23)
        val Dozen = Amount(12.0)
        val Gross = Amount(144.0)
    }
    val mole get() = amount / Mole.amount
    val dozen get() = amount / Dozen.amount
    val gross get() = amount / Gross.amount


    operator fun plus(other: Amount): Amount {
        return Amount(amount + other.amount)
    }
    operator fun minus(other: Amount): Amount {
        return Amount(amount - other.amount)
    }
    operator fun times(scalar: Number): Amount {
        return Amount(amount * scalar.toDouble())
    }
    operator fun div(scalar: Number): Amount {
        return Amount(amount / scalar.toDouble())
    }
    operator fun unaryMinus(): Amount {
        return Amount(-amount)
    }
    operator fun compareTo(other: Amount): Int {
        return amount.compareTo(other.amount)
    }
}