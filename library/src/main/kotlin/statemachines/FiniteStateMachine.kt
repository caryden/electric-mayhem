package statemachines

import actions.Action
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import statemachines.builders.TransitionListBuilder
import sun.rmi.runtime.Log
import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

class FiniteStateMachine<E : Enum<E>>(val initialState: E, val initialAction : actions.Action = actions.Action.NoAction, dispatcher: CoroutineDispatcher = Dispatchers.Default) : Closeable {
    private val finiteStateMachineScope  : CoroutineScope = CoroutineScope(dispatcher + SupervisorJob())
    private val stateTransitionMap = HashMap<E, MutableList<Transition<E>>>()
    private val transitionFlow = MutableSharedFlow<Transition<E>>(0,10, BufferOverflow.DROP_OLDEST)
    private val atomicCurrentState = AtomicReference<E>(initialState)

    var currentState : E
        get() = atomicCurrentState.get()
        private set(value) = atomicCurrentState.set(value)

    init {
        finiteStateMachineScope.launch {
            // run the initialAction to get us into the initialState
            initialAction.execute()

            // then collect triggered transitions
            transitionFlow.collect {
                it.action.execute()
                atomicCurrentState.set(it.toState)
            }
        }
    }

    fun shutdown() {
        finiteStateMachineScope.cancel()
    }
    override fun close() {
        shutdown()
    }
    fun transitions(buildTransitions: TransitionListBuilder<E>.() -> Unit) {
        val transitionListBuilder = TransitionListBuilder<E>()
        transitionListBuilder.apply(buildTransitions)
        addTransitions(transitionListBuilder.list)
    }
    private fun addTransitions(toAdd : List<Transition<E>>) {
        for (t in toAdd)
            addTransition(t)
    }
    private fun addTransition(transition: Transition<E>) {
        val transitions = stateTransitionMap.computeIfAbsent(transition.fromState) { mutableListOf() }

        if (transitions.any { it.toState == transition.toState })
            throw IllegalArgumentException("Transition from ${transition.fromState} to ${transition.toState} already exists!")

        transitions.add(transition)
        // for each transition launch a coroutine to monitor the trigger,
        // if a trigger occurs when we are in the fromState, then we need to execute the Action
        // and move to the new state
        finiteStateMachineScope.launch {
            transition.trigger
                .onEach { println("received trigger $it") }
                .filter { transition.fromState == currentState }
                .map { transition }
                .collect {
                    println("transition.trigger collected ${transition.toString()} ")
                    transitionFlow.emit(it)
                }
        }
    }
}
fun <E : Enum<E>>fsm(initialState : E, builder: FiniteStateMachine<E>.() -> Unit) : FiniteStateMachine<E> {
    return FiniteStateMachine<E>(initialState).apply(builder)
}