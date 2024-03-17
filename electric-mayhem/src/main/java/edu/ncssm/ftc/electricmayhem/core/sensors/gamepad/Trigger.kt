package edu.ncssm.ftc.electricmayhem.core.sensors.gamepad

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import edu.ncssm.ftc.electricmayhem.core.sensors.SensorFlow

class Trigger(trigger: () -> Float, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : SensorFlow<Float>(trigger, 100L, dispatcher) {

}