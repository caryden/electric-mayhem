package sensors

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.Closeable

open class Sensor<T>(private val getValue: () -> T, pollingMs : Long = 100L, dispatcher: CoroutineDispatcher = Dispatchers.IO) : Closeable {
    val value : T
        get() = stateflow.value
    private val stateflow = MutableStateFlow<T>(getValue())
    val flow  = stateflow.asStateFlow()
    val sensorScope  = CoroutineScope(dispatcher + SupervisorJob())
    init {
        sensorScope.launch {
            while (isActive) {
                stateflow.value = getValue()
                delay(pollingMs)
            }
        }
    }
    fun shutdown() {
        if(sensorScope.isActive)
            sensorScope.cancel()
    }
    override fun close() {
        shutdown()
    }
 }