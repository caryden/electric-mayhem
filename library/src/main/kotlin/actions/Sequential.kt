package actions

import kotlinx.coroutines.coroutineScope

class Sequential(vararg val actions : actions.Action) : actions.Action() {
    override suspend fun execute() {
        for (a in actions)
            a.execute()
    }
}