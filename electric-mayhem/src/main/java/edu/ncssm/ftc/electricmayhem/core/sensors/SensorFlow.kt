package sensors

import edu.ncssm.ftc.electricmayhem.core.sensors.Sensor
import edu.ncssm.ftc.electricmayhem.core.sensors.data.SensorData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.io.Closeable

open class SensorFlow<T>(private val getValue: () -> T, pollingMs : Long = 100L, private val sensorScope: CoroutineScope)
    : StateFlow<SensorData<T>>, Sensor {
    constructor(getValue: () -> T, pollingMs : Long = 100L, dispatcher: CoroutineDispatcher = Dispatchers.IO)
            : this(getValue, pollingMs, CoroutineScope(dispatcher))
    private val stateflow = MutableStateFlow(SensorData<T>(getValue(), System.nanoTime()))
    init {
        sensorScope.launch {
            while (isActive) {
                stateflow.value = SensorData(getValue(), System.nanoTime())
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
    fun asRawSensorFlow() : Flow<T> {
        return stateflow.map { it.value }
    }
 }