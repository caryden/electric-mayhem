package edu.ncssm.ftc.electricmayhem.core.behaviortrees.conditions

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import sensors.SensorFlow
import java.io.Closeable

class ConditionNode(private val conditionFlow : Flow<Boolean>, initialCondition : Boolean = false, dispatcher: CoroutineDispatcher = Dispatchers.Default)
    : Node(), Closeable {
    constructor(sensorFlow: SensorFlow<Boolean>, initialCondition : Boolean = false, dispatcher: CoroutineDispatcher = Dispatchers.Default)
            : this(sensorFlow.asRawSensorFlow(), initialCondition, dispatcher)

    private val conditionScope = CoroutineScope(dispatcher)
    private  val statusFlow = MutableStateFlow<NodeStatus>(determineNodeStatus(initialCondition))
    override val status = statusFlow.asStateFlow()
    private fun determineNodeStatus(value : Boolean) : NodeStatus {
        return if (value) NodeStatus.Success else NodeStatus.Failure
    }
    init {
        conditionScope.launch {
            conditionFlow.collect {
                statusFlow.value = determineNodeStatus(it)
            }
        }
    }
    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        return statusFlow.value
    }
    override fun close() {
        conditionScope.cancel()
    }
}