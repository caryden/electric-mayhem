package actions

class Generic(val toExecute : suspend () -> Unit) : actions.Action() {
    override suspend fun execute() {
        toExecute()
    }
}