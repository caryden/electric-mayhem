package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActionNode(private val toExecute : suspend () -> NodeStatus)
    : Node {
    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()

    override suspend fun tick(): NodeStatus {
        try {
            statusFlow.value = toExecute()
        } catch (c : CancellationException) {
            statusFlow.value = NodeStatus.Cancelled
            throw c
        } catch (e : Exception) {
            statusFlow.value = NodeStatus.Failure
        }
        return statusFlow.value
    }
}