package sensors

import com.qualcomm.robotcore.hardware.Gamepad
import edu.ncssm.ftc.electricmayhem.core.sensors.data.JoystickData

class GamePadFlow(private val gamepad : Gamepad) {
    val x = GamePadButton( { gamepad.x  })
    val y = GamePadButton( { gamepad.y })
    val a = GamePadButton( { gamepad.a })
    val b = GamePadButton( { gamepad.b })

    val circle = GamePadButton( { gamepad.circle })
    val triangle = GamePadButton( { gamepad.triangle })
    val square = GamePadButton( { gamepad.square })
    val cross = GamePadButton( { gamepad.cross })

    val dpadUp = GamePadButton( { gamepad.dpad_up })
    val dpadDown = GamePadButton( { gamepad.dpad_down })
    val dpadLeft = GamePadButton( { gamepad.dpad_left })
    val dpadRight = GamePadButton( { gamepad.dpad_right })


    val rightBumper = GamePadButton( { gamepad.right_bumper })
    val leftBumper = GamePadButton( { gamepad.left_bumper })

    val rightTrigger = edu.ncssm.ftc.electricmayhem.core.sensors.Trigger({ gamepad.right_trigger })
    val leftTrigger = edu.ncssm.ftc.electricmayhem.core.sensors.Trigger({ gamepad.left_trigger })

    val rightJoystick = Joystick({ JoystickData(gamepad.right_stick_x, gamepad.right_stick_y) })
    val leftJoystick = Joystick({ JoystickData(gamepad.right_stick_x, gamepad.right_stick_y) })

    val rightJoystickButton = GamePadButton( { gamepad.right_stick_button })


}

