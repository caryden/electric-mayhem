package edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Cooldown(override val child: Node, private val coolDownMillis: Long)
    : DecoratorNode() {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()
    private var lastTickTime = 0L

    override suspend fun tick(tickContext: TickContext): NodeStatus {
        tickContext.tickedNodes.add(this)
        statusFlow.value = NodeStatus.Running
        val currentTime = System.currentTimeMillis()
        val remainingCoolDownMillis = lastTickTime + coolDownMillis - currentTime

        if(remainingCoolDownMillis > 0)
            delay(remainingCoolDownMillis)

        lastTickTime = currentTime
        statusFlow.value = child.tick(tickContext)
        return statusFlow.value
    }
}