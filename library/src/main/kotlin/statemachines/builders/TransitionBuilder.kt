package statemachines.builders

import actions.Action
import kotlinx.coroutines.flow.Flow
import statemachines.Transition

class TransitionBuilder<T : Enum<T>>() {
    private var flow : Flow<Boolean>? = null
    private var fromState : T? = null
    private var toState : T? = null
    private var doAction : Action? = null

    fun on(onFlow: Flow<Boolean>) {
        flow = onFlow
    }
    fun from(state : T){
        fromState = state
    }
    fun to(state: T) {
        toState =state
    }
    fun action(action : Action) {
        doAction = action
    }
    fun build() : Transition<T> {
        return Transition<T>(fromState!!, toState!!, flow!!, doAction!!)
    }

}