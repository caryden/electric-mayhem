package actions

import edu.ncssm.ftc.electricmayhem.core.general.Actionable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class Parallel(vararg val actions : Actionable) : Action( {
    coroutineScope {
        actions
            .map { async { it.execute() } }
            .awaitAll()
        }
    }) {

}