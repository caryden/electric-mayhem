package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable

interface Node  {
    val status: StateFlow<NodeStatus>
    suspend fun tick() : NodeStatus
}