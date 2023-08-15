package edu.ncssm.ftc.electric_mayhem.core.sensors

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import sensors.Sensor

class Trigger(trigger: () -> Float, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : Sensor<Float>(trigger, 100L, dispatcher) {

}