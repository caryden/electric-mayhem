package actions

class Generic(val toExecute : suspend () -> Unit) : Action() {
    override suspend fun execute() {
        toExecute()
    }
}