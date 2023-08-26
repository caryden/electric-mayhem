package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.subsystems.SubsystemCommand
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import subsystems.Subsystem

class ActionNode(private val toExecute : suspend () -> NodeStatus)
    : Node {
    constructor(subsystemCommand: SubsystemCommand) : this( {
        subsystemCommand.execute()
        NodeStatus.Success
    })

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