package edu.ncssm.ftc.electric_mayhem.fsm.sensors

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import sensors.JoystickValues
import sensors.Sensor

class Trigger(trigger: () -> Float, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : Sensor<Float>(trigger, 100L, dispatcher) {

}