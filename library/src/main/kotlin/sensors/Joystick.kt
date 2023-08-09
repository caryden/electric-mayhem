package sensors

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Joystick(joystick: () -> JoystickValues, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : Sensor<JoystickValues>(joystick, 100L, dispatcher) {

}