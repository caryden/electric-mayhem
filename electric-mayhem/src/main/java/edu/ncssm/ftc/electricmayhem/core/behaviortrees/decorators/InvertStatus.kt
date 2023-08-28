package edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvertStatus(override val child: Node) : DecoratorNode {
    override val status : Flow<NodeStatus> = child.status.map { invert(it) }
    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        return invert(child.tick(tickContext))
    }
    private fun invert(status: NodeStatus) : NodeStatus {
        return when (status) {
            NodeStatus.Success -> NodeStatus.Failure
            NodeStatus.Failure -> NodeStatus.Success
            else -> status
        }
    }
}