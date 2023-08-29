package edu.ncssm.ftc.electricmayhem.core.behaviortrees.general

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class  Node  {
    var description = ""

    abstract val status: StateFlow<NodeStatus>
    abstract suspend fun tick(tickContext: TickContext) : NodeStatus
    override fun toString() = "${this::class.simpleName}(${status.value}): $description"
}