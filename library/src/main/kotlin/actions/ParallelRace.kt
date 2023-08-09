package actions

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select

class ParallelRace(vararg val actions: actions.Action) : actions.Action() {
    override suspend fun execute() = coroutineScope {
        val jobs = actions.map { async { it.execute() } }
        select<Unit> {
            jobs.forEach { job ->
                job.onAwait {
                    jobs.forEach { it.cancel() }
                }
            }
        }
    }
}
