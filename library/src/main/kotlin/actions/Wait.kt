package actions

import kotlinx.coroutines.delay

class Wait(millis : Long) : Action({ delay(millis) }) {

}