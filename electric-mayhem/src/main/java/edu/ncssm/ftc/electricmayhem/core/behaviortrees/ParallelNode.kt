package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParallelNode(vararg children : Node)
    : ControlNode {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()
    override val children = children.toList()
    override suspend fun tick() : NodeStatus {
        statusFlow.value = NodeStatus.Running
        try {
            val results  = coroutineScope { children.map { async { it.tick() } }.awaitAll() }
            statusFlow.value =
                if (results.all { it == NodeStatus.Success })
                    NodeStatus.Success
                else if (results.any { it == NodeStatus.Cancelled })
                    NodeStatus.Cancelled
                else
                    NodeStatus.Failure

        } catch (c : CancellationException) {
            statusFlow.value = NodeStatus.Cancelled
            throw c
        } catch (e : Exception) {
            statusFlow.value = NodeStatus.Failure
        }
        return statusFlow.value
    }
}