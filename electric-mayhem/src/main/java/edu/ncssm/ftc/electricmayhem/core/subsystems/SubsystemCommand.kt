package edu.ncssm.ftc.electricmayhem.core.subsystems

import actions.Action
import edu.ncssm.ftc.electricmayhem.core.general.Actionable
import subsystems.Subsystem

abstract class SubsystemCommand(private val parentSubsystem : Subsystem, private val toExecute : suspend () -> Unit) :
    Actionable {
    override suspend fun execute() {
        parentSubsystem.executeSubsystemAction { toExecute() }
    }
}