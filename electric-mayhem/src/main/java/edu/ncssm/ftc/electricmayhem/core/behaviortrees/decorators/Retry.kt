package edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Retry(override val child: Node, val upToTimes: Int) : DecoratorNode {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()

    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        statusFlow.value = NodeStatus.Running
        repeat(upToTimes) {
            val status = child.tick(tickContext)
            if (status == NodeStatus.Success) {
                statusFlow.value = status
                return NodeStatus.Success
            }
        }
        return NodeStatus.Failure
    }
}