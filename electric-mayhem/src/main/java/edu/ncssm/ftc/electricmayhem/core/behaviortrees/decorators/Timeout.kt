package edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.selects.select

class Timeout(override val child: Node, val timeoutMillis: Long)
    : DecoratorNode() {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()
    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        statusFlow.value = NodeStatus.Running

        // call the child node tick in a coroutine via async
        // while calling another coroutine in parallel to delay for the timeout and then return NodeStatus.Failure
        // take the result from teh first one to finish and cancel the other one
        try {
            statusFlow.value = withTimeout(timeoutMillis) { child.tick(tickContext) }
        } catch (t : TimeoutCancellationException) {
            statusFlow.value = NodeStatus.Failure
        }
        return statusFlow.value
    }
}