package edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RepeatUntil(override val child: Node, val repeatUnitlStatus: NodeStatus)
    : DecoratorNode() {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()
    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        statusFlow.value = NodeStatus.Running
        while (true) {
            val status = child.tick(tickContext)
            if (status == repeatUnitlStatus) {
                statusFlow.value = NodeStatus.Success
                return statusFlow.value
            }
        }
    }

}