package actions

abstract class Action {
    abstract suspend fun execute()
    object NoAction : Action() {
        override suspend fun execute() = Unit
    }
}