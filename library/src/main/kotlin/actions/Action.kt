package actions


 open class Action(private val toExecute : suspend () -> Unit) {
     open suspend fun execute() {
         toExecute()
     }
    object NoAction : Action( { } )
}