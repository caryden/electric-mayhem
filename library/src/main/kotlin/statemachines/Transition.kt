package statemachines

import actions.Action
import kotlinx.coroutines.flow.Flow

class Transition<T: Enum<T>>( val fromState : T, val toState : T, val trigger: Flow<Boolean>, val action: Action = Action.NoAction) {
    override fun toString(): String {
        return "transition: ${this.fromState} -> ${this.toState}"
    }
}


