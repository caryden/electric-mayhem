package sensors

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class GamePad {
    var isXKeyPressed : Boolean = false
    val x = GamePadButton( { isXKeyPressed }).flow

    var isYKeyPressed : Boolean = false
    val y = GamePadButton( { isYKeyPressed }).flow

    var rightJoystickValues = JoystickValues(0.0,0.0)
    val rightJoystick = Joystick({ rightJoystickValues })

}

