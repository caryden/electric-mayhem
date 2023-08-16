package edu.ncssm.ftc.electricmayhem.core.actuators

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import sensors.window
import kotlin.math.abs

open class MotorFlow(setValue : (Double) -> Unit, defaultValue : Double = 0.0, tolerance : Double = 0.01, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : StateFlow<Double>, Actuator {

    private val stateFlow = MutableStateFlow(defaultValue)
    private val actuatorScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    init {
        actuatorScope.launch {
            try {
                stateFlow
                    .window(2)
                    .filter {
                        val (previous, current) = it
                        abs(previous - current) > tolerance
                    }.map { it.last() }
                    .collectLatest {
                        // here we actually send the command to the motor, but only if the power has changed meaningfully (see the filter above)
                        // also note that we use collectLatest so that if the power changes again before the previous command has been sent, we cancel the previous command
                        setValue(it)
                    }
            } finally {
                // this block will run even if the coroutine is cancelled and return the actuator to a safe state
                setValue(defaultValue)
            }
        }
    }
    override val replayCache: List<Double>
        get() = stateFlow.replayCache
    override var value: Double
        get() = stateFlow.value
        set(value) { stateFlow.value = value }
    override suspend fun collect(collector: FlowCollector<Double>): Nothing {
        stateFlow.collect(collector)
    }
    override fun close() {
        if(actuatorScope.isActive)
            actuatorScope.cancel()
    }
}