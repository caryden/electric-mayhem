package edu.ncssm.ftc.electricmayhem.core.behaviortrees.general

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Node  {
    val status: StateFlow<NodeStatus>
    suspend fun tick(tickContext: TickContext) : NodeStatus
}