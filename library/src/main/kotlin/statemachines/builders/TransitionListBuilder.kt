package statemachines.builders

import statemachines.FiniteStateMachineStates
import statemachines.Transition

class TransitionListBuilder<T : FiniteStateMachineStates>() {
    private val _flatTransitionList = ArrayList<Transition<T>>()
    val list : List<Transition<T>> = _flatTransitionList

    fun whenIn(state : T, buildOn : FromBuilder<T>.OnBuilder.() -> Unit) {
        val fromBuilder = FromBuilder<T>(state) { t -> _flatTransitionList.add(t) }
        val onBuilder = fromBuilder.OnBuilder().apply(buildOn)
    }
}
