package actions

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class Parallel(vararg val actions : actions.Action) : actions.Action() {
    override suspend fun execute() {
        coroutineScope {
            actions
                .map { async { it.execute() } }
                .awaitAll()
        }
    }
}