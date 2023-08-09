package actions

import subsystems.Subsystem

abstract class SubsystemAction(private val parentSubsystem : Subsystem) : Action() {

    // subsystem actions need to be executed one-by-one by the parent subsystem
    final override suspend fun execute() {
        parentSubsystem.executeSubsystemAction(this)
    }
    abstract suspend fun subsystemExecute()

}