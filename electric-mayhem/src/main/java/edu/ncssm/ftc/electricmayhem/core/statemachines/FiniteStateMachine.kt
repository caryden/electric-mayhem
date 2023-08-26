package edu.ncssm.ftc.electricmayhem.core.statemachines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import statemachines.builders.TransitionListBuilder


class FiniteStateMachine<S : FiniteStateMachineStates>(initialState: S, dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    StateMachine {
    private val finiteStateMachineScope  : CoroutineScope = CoroutineScope(dispatcher + SupervisorJob())
    private val stateTransitionMap = HashMap<S, MutableList<Transition<S>>>()
    private val transitionFlow = MutableSharedFlow<Transition<S>>(0,10, BufferOverflow.DROP_OLDEST)
    private val currentStateFlow = MutableStateFlow<S>(initialState)

    val currentState : S
        get() = currentStateFlow.value

    override fun start() {
        finiteStateMachineScope.launch {
            // start the enter action from the initial state
            currentStateFlow.value.actionOnEnter.execute()

            // then collect triggered transitions
            transitionFlow.collect {transition ->

                // start the exit action from the current state
                currentStateFlow.value.actionOnExit.execute()

                // start the transition action
                transition.action.execute()
                currentStateFlow.value = transition.toState

                // start the enter action for the new state
                currentStateFlow.value.actionOnEnter.execute()
            }
        }
    }
    fun shutdown() {
        finiteStateMachineScope.cancel()
    }
    override fun close() {
        shutdown()
    }
    fun transitions(buildTransitions: TransitionListBuilder<S>.() -> Unit) {
        val transitionListBuilder = TransitionListBuilder<S>()
        transitionListBuilder.apply(buildTransitions)
        addTransitions(transitionListBuilder.list)
    }
    private fun addTransitions(toAdd : List<Transition<S>>) {
        for (t in toAdd)
            addTransition(t)
    }
    private fun addTransition(transition: Transition<S>) {
        val transitions = stateTransitionMap.computeIfAbsent(transition.fromState) { mutableListOf() }

        if (transitions.any { it.toState == transition.toState })
            throw IllegalArgumentException("Transition from ${transition.fromState} to ${transition.toState} already exists!")

        transitions.add(transition)
        // for each transition launch a coroutine to monitor the trigger,
        // if a trigger occurs when we are in the fromState, then we need to start the Action
        // and move to the new state
        finiteStateMachineScope.launch {
            transition.trigger
                .filter { transition.fromState == currentState }
                .map { transition }
                .collect {
                    transitionFlow.emit(it)
                }
        }
    }
}
fun <E : FiniteStateMachineStates>fsm(initialState : E,
                                      dispatcher: CoroutineDispatcher = Dispatchers.Default,
                                      builder: FiniteStateMachine<E>.() -> Unit) : FiniteStateMachine<E> {
    return FiniteStateMachine<E>(initialState, dispatcher).apply(builder)
}