package actions

class Sequential(vararg val actions : Action) : Action() {
    override suspend fun execute() {
        for (a in actions)
            a.execute()
    }
}