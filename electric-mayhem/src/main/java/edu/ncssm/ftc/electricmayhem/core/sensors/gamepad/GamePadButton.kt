package edu.ncssm.ftc.electricmayhem.core.sensors.gamepad

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import edu.ncssm.ftc.electricmayhem.core.sensors.SensorFlow
import edu.ncssm.ftc.electricmayhem.core.sensors.data.ButtonState

class GamePadButton(button : () -> Boolean, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : SensorFlow<ButtonState>({ ButtonState.fromBoolean(button()) }, 100L, dispatcher) {

}