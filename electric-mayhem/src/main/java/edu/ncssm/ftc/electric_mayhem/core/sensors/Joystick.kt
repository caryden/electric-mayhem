package sensors

import edu.ncssm.ftc.electric_mayhem.core.sensors.data.JoystickData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Joystick(joystick: () -> JoystickData, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : Sensor<JoystickData>(joystick, 100L, dispatcher) {

}