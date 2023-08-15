package sensors

import edu.ncssm.ftc.electricmayhem.core.sensors.data.JoystickData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Joystick(joystick: () -> JoystickData, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : SensorFlow<JoystickData>(joystick, 100L, dispatcher) {

}