package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
 class SequenceNode(vararg children : Node)
    : ControlNode {
     private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
     override val status = statusFlow.asStateFlow()
     override val children = children.toList()
    override suspend fun tick() : NodeStatus {
        statusFlow.value = NodeStatus.Running
        try {
            for (child in children) {
                val status = child.tick()
                if (status != NodeStatus.Success) {
                    statusFlow.value = status
                    return statusFlow.value
                }
            }
            statusFlow.value = NodeStatus.Success
        } catch (c : CancellationException) {
            statusFlow.value = NodeStatus.Cancelled
            throw c
        } catch (e : Exception) {
            statusFlow.value = NodeStatus.Failure
        }
        return statusFlow.value
    }
}