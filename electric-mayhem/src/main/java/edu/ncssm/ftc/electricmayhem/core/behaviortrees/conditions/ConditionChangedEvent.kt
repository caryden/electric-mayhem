package edu.ncssm.ftc.electricmayhem.core.behaviortrees.conditions

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

data class ConditionChangedEvent(val condition: ConditionNode?, val newStatus: NodeStatus) {
    @OptIn(ExperimentalTime::class)
    val timestamp = TimeSource.Monotonic.markNow()
}
