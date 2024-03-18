package edu.ncssm.ftc.electricmayhem.core.subsystems

import actions.Action
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.general.Actionable
import subsystems.Subsystem

//private val toExecute : suspend () -> NodeStatus
abstract class SubsystemCommand(private val parentSubsystem : Subsystem) : Actionable {

    abstract suspend fun action() : NodeStatus
    final override suspend fun execute() : NodeStatus {
        return parentSubsystem.executeSubsystemAction { action() }
    }
}