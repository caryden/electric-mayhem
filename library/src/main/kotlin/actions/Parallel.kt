package actions

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class Parallel(vararg val actions : Action) : Action( {
    coroutineScope {
        actions
            .map { async { it.execute() } }
            .awaitAll()
        }
    }) {

}