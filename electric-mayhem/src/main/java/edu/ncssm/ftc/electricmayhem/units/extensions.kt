package edu.ncssm.ftc.electricmayhem.units

import edu.ncssm.ftc.electricmayhem.units.base.Angle
import edu.ncssm.ftc.electricmayhem.units.base.BaseUnit
import edu.ncssm.ftc.electricmayhem.units.base.Length
import edu.ncssm.ftc.electricmayhem.units.base.Mass
import edu.ncssm.ftc.electricmayhem.units.base.Time
import edu.ncssm.ftc.electricmayhem.units.derived.Derived
import edu.ncssm.ftc.electricmayhem.units.derived.ElectricCharge
import edu.ncssm.ftc.electricmayhem.units.derived.Energy
import edu.ncssm.ftc.electricmayhem.units.derived.Force
import edu.ncssm.ftc.electricmayhem.units.derived.Power
import edu.ncssm.ftc.electricmayhem.units.derived.Resistance
import edu.ncssm.ftc.electricmayhem.units.derived.Velocity
import edu.ncssm.ftc.electricmayhem.units.derived.Voltage


operator fun Number.times(unit: Length): Length {
    return unit.times(this.toDouble())
}
operator fun Number.times(unit: Mass): Mass {
    return unit.times(this.toDouble())
}
operator fun Number.times(unit: Time): Time {
    return unit.times(this)
}
operator fun Number.times(unit: Force): Force {
    return unit.times(this)
}
operator fun Number.times(unit: Energy): Energy {
    return unit.times(this)
}
operator fun Number.times(unit: Voltage): Voltage {
    return unit.times(this)
}
operator fun Number.times(unit: Velocity): Velocity {
    return unit.times(this)
}
operator fun Number.times(unit: Resistance): Resistance {
    return unit.times(this)
}
operator fun Number.times(unit: Power): Power {
    return unit.times(this)
}
operator fun Number.times(unit: ElectricCharge): ElectricCharge {
    return unit.times(this)
}
operator fun Number.times(unit: Angle): Angle {
    return unit.times(this)
}
operator fun Number.times(unit: Derived): Derived {
    return unit.times(this.toDouble())
}

infix fun BaseUnit.per(unit: BaseUnit): BaseUnit {
    return this / unit
}
fun BaseUnit.squared(): BaseUnit {
    return Derived(Math.pow(value, 2.0), List(2) { this })
}

