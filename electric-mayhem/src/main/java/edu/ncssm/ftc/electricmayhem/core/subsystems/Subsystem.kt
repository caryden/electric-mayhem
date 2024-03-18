package subsystems

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.sensors.Sensor
import edu.ncssm.ftc.electricmayhem.core.statemachines.StateMachine
import kotlinx.coroutines.*
import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

abstract class Subsystem(dispatcher: CoroutineDispatcher = Dispatchers.Default) : Closeable {

    protected val subsystems = ArrayList<Subsystem>()
    protected val sensors = ArrayList<Sensor>()
    protected val subsystemScope = CoroutineScope(dispatcher + SupervisorJob())
    private var currentSubsystemJob = AtomicReference<Job?>(null)

    suspend fun executeSubsystemAction(action: suspend () -> NodeStatus): NodeStatus {
        // cancel any existing job running for this subsystem if not already finished
        // and join to ensure it is completed
        val job = currentSubsystemJob.get()
        if (null != job && !job.isCompleted)
            job.cancelAndJoin()

        val newJob = subsystemScope.async {
            try {
                action()
            } catch (e: CancellationException) {
                NodeStatus.Cancelled
            } catch (e: Exception) {
                NodeStatus.Failure
            }
        }
        currentSubsystemJob.set(newJob)

        // Wait for the action to complete and return its result
        return newJob.await()
    }
    open fun start() {
        for (s in subsystems)
            s.start()
    }
    override fun close() {
        if(subsystemScope.isActive)
            subsystemScope.cancel()

        for (s in subsystems)
            s.close()

        for (s in sensors)
            s.close()
    }

}
