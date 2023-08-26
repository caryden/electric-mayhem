package actions

import edu.ncssm.ftc.electricmayhem.core.general.Actionable

class Sequential(vararg val actions : Actionable) : Action({
    for (a in actions)
        a.execute()
}) {
}