package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

data class ConditionChangedEvent(val condition: ConditionNode?, val newStatus: NodeStatus) {
    @OptIn(ExperimentalTime::class)
    val timestamp = TimeSource.Monotonic.markNow()
}
