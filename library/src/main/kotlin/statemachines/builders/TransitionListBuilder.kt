package statemachines.builders

import statemachines.Transition

class TransitionListBuilder<T : Enum<T>>() {
    private val _flatTransitionList = ArrayList<Transition<T>>()
    val list : List<Transition<T>> = _flatTransitionList

    fun transition(buildTransition: TransitionBuilder<T>.() -> Unit) {
        val transistion = TransitionBuilder<T>().apply(buildTransition).build()
        _flatTransitionList.add(transistion)
    }

    fun whenIn(state : T, buildOn : FromBuilder<T>.OnBuilder.() -> Unit) {
        val fromBuilder = FromBuilder<T>(state) { t -> _flatTransitionList.add(t) }
        val onBuilder = fromBuilder.OnBuilder().apply(buildOn)
    }

    fun from(state : T) : FromBuilder<T>.OnBuilder {
        val fromBuilder = FromBuilder<T>(state) { t -> _flatTransitionList.add(t) }
        return fromBuilder.OnBuilder()
    }
}
