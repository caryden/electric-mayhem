package actions

import android.util.Log
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.general.Actionable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout


open class Action(private val toExecute : suspend () -> Unit) : Actionable {
    private val tag = this::class.simpleName
    private val noTimeoutEnabled = 0L
    internal var timeoutMs : Long = noTimeoutEnabled
    internal var onTimeout : suspend () -> Unit = { Log.d(tag,"No onTimeout specified.") }
    override fun toString() = this::class.simpleName ?: super.toString()
     override suspend fun execute(): NodeStatus {
         try {
             return if(timeoutMs > noTimeoutEnabled)
                 withTimeout(timeoutMs) {
                     toExecute()
                     NodeStatus.Success
                 }
             else {
                 toExecute()
                 NodeStatus.Success
             }

         } catch (ex : TimeoutCancellationException) {
             onTimeout()
             Log.d(tag,"Timed out with timeout of $timeoutMs")
             return NodeStatus.Success
         } catch (ex : CancellationException) {
             return NodeStatus.Cancelled
         } catch (ex : Exception) {
             return NodeStatus.Failure
         }
     }
    object NoAction : Action( { } )
}

fun Action.withTimeout(timeout: Long) : Action {
    this.timeoutMs = timeout
    return this
}
fun Action.onTimeout(action : suspend () -> Unit) : Action {
    this.onTimeout = action
    return this
}

