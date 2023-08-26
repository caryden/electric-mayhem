package actions

import android.util.Log
import edu.ncssm.ftc.electricmayhem.core.general.Actionable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout


open class Action(private val toExecute : suspend () -> Unit) : Actionable {
    private val tag = this::class.simpleName
    private val noTimeoutEnabled = 0L
    internal var timeoutMs : Long = noTimeoutEnabled
    internal var onTimeout : suspend () -> Unit = { Log.d(tag,"No onTimeout specified.") }
    override fun toString() = this::class.simpleName ?: super.toString()
     override suspend fun execute() {
         var timedOut = false
         try {
             if(timeoutMs > noTimeoutEnabled)
                 withTimeout(timeoutMs){ toExecute() }
             else
                 toExecute()

         } catch (ex : TimeoutCancellationException) {
             timedOut = true
             Log.d(tag,"Timed out with timeout of $timeoutMs")
         }

         if(timedOut)
             onTimeout()
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

