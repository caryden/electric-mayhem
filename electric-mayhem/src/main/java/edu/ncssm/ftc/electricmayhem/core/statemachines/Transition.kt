package edu.ncssm.ftc.electricmayhem.core.statemachines

import actions.Action
import edu.ncssm.ftc.electricmayhem.core.general.Actionable
import kotlinx.coroutines.flow.Flow

class Transition<T: FiniteStateMachineStates>(val fromState : T, val toState : T, val trigger: Flow<Boolean>, val action: Actionable = Action.NoAction) {
    override fun toString(): String {
        return "transition: ${this.fromState} -> ${this.toState}"
    }
}


