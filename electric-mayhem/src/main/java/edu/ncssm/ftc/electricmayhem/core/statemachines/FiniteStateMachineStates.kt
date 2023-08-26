package edu.ncssm.ftc.electricmayhem.core.statemachines

import actions.Action

abstract class FiniteStateMachineStates(val actionOnEnter : Action = Action.NoAction,
                                        val actionOnExit : Action = Action.NoAction ) {
    override fun toString() = this::class.simpleName ?: super.toString()
}