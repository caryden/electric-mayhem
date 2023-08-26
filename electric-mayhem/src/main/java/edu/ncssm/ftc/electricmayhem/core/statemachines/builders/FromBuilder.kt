package statemachines.builders

import actions.Action
import edu.ncssm.ftc.electricmayhem.core.general.Actionable
import kotlinx.coroutines.flow.Flow
import edu.ncssm.ftc.electricmayhem.core.statemachines.FiniteStateMachineStates
import edu.ncssm.ftc.electricmayhem.core.statemachines.Transition

class FromBuilder<T : FiniteStateMachineStates>(val fromState : T, val complete : (Transition<T>) -> Unit) {
    var condition : Flow<Boolean>? = null
    var toState : T? = null
    var action : Actionable? = null

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
        fun andDo(a : Actionable) {
            action = a
            val transition = build()
            complete(transition)
        }
    }
}




