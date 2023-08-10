package actions

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout


open class Action(private val toExecute : suspend () -> Unit) {

    internal var timeoutMs : Long = 1000L
    override fun toString() = this::class.simpleName ?: super.toString()
     open suspend fun execute() {
         try {
             withTimeout(timeoutMs){
                 toExecute()
             }
         } catch (ex : TimeoutCancellationException) {
             // TODO: I need to figure out how to send this to Log.i but that will take me into Log hell i think
            println("$this timed out with timeout of $timeoutMs")
         }
     }
    object NoAction : Action( { } )
}

fun Action.withTimeout(timeout: Long) : Action {
    this.timeoutMs = timeout
    return this
}

