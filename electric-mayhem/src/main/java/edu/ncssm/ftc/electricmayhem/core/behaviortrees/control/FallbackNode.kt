package edu.ncssm.ftc.electricmayhem.core.behaviortrees.control

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FallbackNode(vararg children : Node)
    : ControlNode {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()
    override val children = children.toList()

    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        statusFlow.value = NodeStatus.Running
        try {
            for (child in children) {
                val status = child.tick(tickContext)
                if (status == NodeStatus.Success) {
                    statusFlow.value = status
                    return statusFlow.value
                }
            }
            statusFlow.value = NodeStatus.Failure
        } catch (c : CancellationException) {
            statusFlow.value = NodeStatus.Cancelled
            throw c
        } catch (e : Exception) {
            statusFlow.value = NodeStatus.Failure
        }
        return statusFlow.value
    }
}
