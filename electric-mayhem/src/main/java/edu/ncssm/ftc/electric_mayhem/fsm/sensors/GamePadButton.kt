package sensors

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GamePadButton(button : () -> Boolean, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : Sensor<Boolean>(button, 100L, dispatcher) {

}