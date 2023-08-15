package sensors

import edu.ncssm.ftc.electric_mayhem.core.sensors.data.SensorData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.Closeable

open class Sensor<T>(private val getValue: () -> T, pollingMs : Long = 100L, dispatcher: CoroutineDispatcher = Dispatchers.IO) : Closeable {
    val value : SensorData<T>
        get() = stateflow.value
    private val stateflow = MutableStateFlow(SensorData<T>(getValue(), System.nanoTime()))
    val flow  = stateflow.asStateFlow()
    val sensorScope  = CoroutineScope(dispatcher + SupervisorJob())
    init {
        sensorScope.launch {
            while (isActive) {
                stateflow.value = SensorData<T>(getValue(), System.nanoTime())
                delay(pollingMs)
            }
        }
    }
    override fun close() {
        if(sensorScope.isActive)
            sensorScope.cancel()
    }
 }