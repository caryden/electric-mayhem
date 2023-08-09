package actions

import kotlinx.coroutines.delay

class Wait(val millis : Long) : actions.Action() {
    override suspend fun execute() {
        delay(millis)
    }
}