package edu.ncssm.ftc.electricmayhem.core.general

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus

interface Actionable {
    suspend fun execute() : NodeStatus
}