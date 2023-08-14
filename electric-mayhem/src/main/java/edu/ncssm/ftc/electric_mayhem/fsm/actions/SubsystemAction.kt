package actions

import subsystems.Subsystem

abstract class SubsystemAction(private val parentSubsystem : Subsystem, toExecute : suspend () -> Unit) : Action(toExecute) {
    override suspend fun execute() {
        parentSubsystem.executeSubsystemAction { super.execute() }
    }
}