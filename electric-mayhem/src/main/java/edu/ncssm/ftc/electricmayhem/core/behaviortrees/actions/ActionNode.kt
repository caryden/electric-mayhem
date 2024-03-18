package edu.ncssm.ftc.electricmayhem.core.behaviortrees.actions

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
import edu.ncssm.ftc.electricmayhem.core.subsystems.SubsystemCommand
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActionNode(private val toExecute : suspend () -> NodeStatus)
    : Node() {
    constructor(subsystemCommand: SubsystemCommand) : this( {
        subsystemCommand.execute()
    })

    private  val statusFlow = MutableStateFlow<NodeStatus>(NodeStatus.Idle)
    override val status = statusFlow.asStateFlow()

    override suspend fun tick(tickContext: TickContext): NodeStatus {
        try {
            tickContext.tickedNodes.add(this)
            statusFlow.value = NodeStatus.Running
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