package subsystems

import actions.SubsystemAction
import edu.ncssm.ftc.electric_mayhem.fsm.statemachines.StateMachine
import kotlinx.coroutines.*
import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

abstract class Subsystem(dispatcher: CoroutineDispatcher = Dispatchers.Default) : Closeable {

    protected val subsystems = ArrayList<Subsystem>()
    var stateMachine = StateMachine.NoStateMachine
    protected val subsystemScope = CoroutineScope(dispatcher + SupervisorJob())
    private var currentSubsystemJob = AtomicReference<Job?>(null)

    suspend fun executeSubsystemAction(action: suspend () -> Unit) {
        // cancel any existing job running for this subsystem if not already finished
        // and join to ensure it is completed
        val job = currentSubsystemJob.get()
        if (null != job && !job.isCompleted)
            job.cancelAndJoin()

        val newJob = subsystemScope.launch {
            action()
        }
        currentSubsystemJob.set(newJob)
    }
    open fun start() {
        for (s in subsystems)
            s.start()

        stateMachine.start()
    }
    override fun close() {
        if(subsystemScope.isActive)
            subsystemScope.cancel()

        stateMachine.close()

        for (s in subsystems)
            s.close()
    }

}
