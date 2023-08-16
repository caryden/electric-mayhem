package edu.ncssm.ftc.electricmayhem.core.sensors.gamepad

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import sensors.SensorFlow

class GamePadButton(button : () -> Boolean, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : SensorFlow<Boolean>(button, 100L, dispatcher) {

}