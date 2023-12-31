package statemachines.builders

import edu.ncssm.ftc.electricmayhem.core.statemachines.FiniteStateMachineStates
import edu.ncssm.ftc.electricmayhem.core.statemachines.Transition

class TransitionListBuilder<T : FiniteStateMachineStates>() {
    private val _flatTransitionList = ArrayList<Transition<T>>()
    val list : List<Transition<T>> = _flatTransitionList

    fun whenIn(state : T, buildOn : FromBuilder<T>.OnBuilder.() -> Unit) {
        val fromBuilder = FromBuilder<T>(state) { t -> _flatTransitionList.add(t) }
        fromBuilder.OnBuilder().apply(buildOn)
    }
}
