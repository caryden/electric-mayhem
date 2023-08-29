package edu.ncssm.ftc.electricmayhem.core.behaviortrees.general

sealed class NodeStatus {
    override fun toString() = this::class.simpleName ?: super.toString()

    object Success : NodeStatus()
    object Failure : NodeStatus()
    object Running : NodeStatus()
    object Idle : NodeStatus()
    object Cancelled : NodeStatus()
}