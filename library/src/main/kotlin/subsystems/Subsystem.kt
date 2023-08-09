package subsystems

import actions.SubsystemAction
import kotlinx.coroutines.*
import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

abstract class Subsystem(dispatcher: CoroutineDispatcher = Dispatchers.Default) : Closeable {
    protected val subsystems = ArrayList<Subsystem>()
    protected val subsystemScope = CoroutineScope(dispatcher + SupervisorJob())
    private var currentSubsystemJob = AtomicReference<Job?>(null)

    suspend fun executeSubsystemAction(action: SubsystemAction) {
        // cancel any existing job running for this subsystem if not already finished
        // and join to ensure it is completed
        val job = currentSubsystemJob.get()
        if (null != job && !job.isCompleted)
            job.cancelAndJoin()

        val newJob = subsystemScope.launch {
            action.subsystemExecute()
        }
        currentSubsystemJob.set(newJob)
    }
    override fun close() {
       shutdown()
    }
    fun shutdown() {
        if(subsystemScope.isActive)
            subsystemScope.cancel()

        for (s in subsystems)
            s.shutdown()
    }
}
