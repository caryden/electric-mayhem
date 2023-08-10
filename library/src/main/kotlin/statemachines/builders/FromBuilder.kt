package statemachines.builders

import actions.Action
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import statemachines.FiniteStateMachineStates
import statemachines.Transition

class FromBuilder<T : FiniteStateMachineStates>(val fromState : T, val complete : (Transition<T>) -> Unit) {
    var condition : Flow<Boolean>? = null
    var toState : T? = null
    var action : Action? = null

    fun build() : Transition<T> {
        return Transition<T>(fromState, toState!!, condition!!, action!!)
    }

    inner class OnBuilder  {
        fun whenever(c : Flow<Boolean>) : ToBuilder {
             condition = c
            return ToBuilder()
        }
    }
    inner class ToBuilder  {
        fun transitionTo(state : T) : ActionBuilder{
            toState = state
            return ActionBuilder()
        }
    }
    inner class ActionBuilder {
        fun andDo(a : actions.Action){
            action = a
            val transition = build()
            complete(transition)
        }
    }
}




