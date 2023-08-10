package actions

class Sequential(vararg val actions : Action) : Action({
    for (a in actions)
        a.execute()
}) {
}