package edu.ncssm.ftc.electricmayhem.core.sensors.gamepad

import edu.ncssm.ftc.electricmayhem.core.sensors.data.ButtonState
import edu.ncssm.ftc.electricmayhem.core.sensors.data.SensorData
import edu.ncssm.ftc.electricmayhem.core.sensors.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

fun Flow<SensorData<ButtonState>>.isPressed() : Flow<Boolean> =
    window(2)
        .transform {
            if(it.first().value == ButtonState.Released && it.last().value == ButtonState.Pressed)
                emit(true)
        }

fun Flow<SensorData<ButtonState>>.isReleased() : Flow<Boolean> =
    window(2)
        .transform {
            if(it.first().value == ButtonState.Pressed && it.last().value == ButtonState.Released)
                emit(true)
        }
