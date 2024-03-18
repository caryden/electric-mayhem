package actions

import edu.ncssm.ftc.electricmayhem.core.general.Actionable

class Sequential(vararg val actions : Actionable) : Action({
    actions.forEach { it.execute() }
}) { }