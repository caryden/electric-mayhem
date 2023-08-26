package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable

class ConditionNode(private val conditionFlow : Flow<Boolean>, initialCondition : Boolean = false, dispatcher: CoroutineDispatcher = Dispatchers.Default)
    : Node, Closeable {
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
    override suspend fun tick() : NodeStatus {
        return statusFlow.value
    }
    override fun close() {
        conditionScope.cancel()
    }
}