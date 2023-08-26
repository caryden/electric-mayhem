package edu.ncssm.ftc.electricmayhem.core.behaviortrees

sealed class NodeStatus {
    object Success : NodeStatus()
    object Failure : NodeStatus()
    object Running : NodeStatus()
    object Idle : NodeStatus()
    object Cancelled : NodeStatus()
}