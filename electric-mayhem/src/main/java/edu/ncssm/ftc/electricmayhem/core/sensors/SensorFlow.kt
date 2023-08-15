package sensors

import edu.ncssm.ftc.electricmayhem.core.sensors.data.SensorData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable

open class SensorFlow<T>(private val getValue: () -> T, pollingMs : Long = 100L, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : StateFlow<SensorData<T>>, Closeable {
    private val stateflow = MutableStateFlow(SensorData<T>(getValue(), System.nanoTime()))
    private val sensorScope  = CoroutineScope(dispatcher + SupervisorJob())
    init {
        sensorScope.launch {
            while (isActive) {
                stateflow.value = SensorData<T>(getValue(), System.nanoTime())
                delay(pollingMs)
            }
        }
    }
    override val value : SensorData<T>
        get() = stateflow.value
    override val replayCache: List<SensorData<T>>
        get() = stateflow.replayCache
    override suspend fun collect(collector: FlowCollector<SensorData<T>>): Nothing {
        stateflow.collect(collector)
    }
    override fun close() {
        if(sensorScope.isActive)
            sensorScope.cancel()
    }
 }