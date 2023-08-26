package actions

import edu.ncssm.ftc.electricmayhem.core.general.Actionable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select

class ParallelRace(vararg val actions: Actionable) : Action({
    coroutineScope {
        val jobs = actions.map { async { it.execute() } }
        select<Unit> {
            jobs.forEach { job ->
                job.onAwait {
                    jobs.forEach { it.cancel() }
                }
            }
        }
    }
}) {
}
