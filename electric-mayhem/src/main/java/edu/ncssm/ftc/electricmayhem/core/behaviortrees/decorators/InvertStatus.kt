package edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class InvertStatus(override val child: Node) : DecoratorNode {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()

    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        statusFlow.value =  invert(child.tick(tickContext))
        return statusFlow.value
    }
    private fun invert(status: NodeStatus) : NodeStatus {
        return when (status) {
            NodeStatus.Success -> NodeStatus.Failure
            NodeStatus.Failure -> NodeStatus.Success
            else -> status
        }
    }
}