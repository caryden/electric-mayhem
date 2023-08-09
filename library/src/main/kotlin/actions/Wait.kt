package actions

import kotlinx.coroutines.delay

class Wait(val millis : Long) : Action() {
    override suspend fun execute() {
        delay(millis)
    }
}