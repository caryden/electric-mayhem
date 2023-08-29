package edu.ncssm.ftc.electricmayhem.core.behaviortrees.control

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParallelNode(vararg children : Node)
    : ControlNode() {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()
    override val children = children.toList()
    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        statusFlow.value = NodeStatus.Running
        try {
            coroutineScope {
                val results = children.map { async { it.tick(tickContext) } }.awaitAll()
                statusFlow.value =
                    if (results.all { it == NodeStatus.Success })
                        NodeStatus.Success
                    else if (results.any { it == NodeStatus.Cancelled })
                        NodeStatus.Cancelled
                    else
                        NodeStatus.Failure
            }

        } catch (c : CancellationException) {
            statusFlow.value = NodeStatus.Cancelled
            throw c
        } catch (e : Exception) {
            statusFlow.value = NodeStatus.Failure
        }
        return statusFlow.value
    }
}