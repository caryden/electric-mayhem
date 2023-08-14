package sensors

import com.qualcomm.robotcore.hardware.Gamepad
import edu.ncssm.ftc.electric_mayhem.fsm.sensors.Trigger

class GamePadFlow(private val gamepad : Gamepad) {
    val x = GamePadButton( { gamepad.x  }).flow
    val y = GamePadButton( { gamepad.y }).flow
    val a = GamePadButton( { gamepad.a }).flow
    val b = GamePadButton( { gamepad.b }).flow

    val circle = GamePadButton( { gamepad.circle }).flow
    val triangle = GamePadButton( { gamepad.triangle }).flow
    val square = GamePadButton( { gamepad.square }).flow
    val cross = GamePadButton( { gamepad.cross }).flow

    val dpadUp = GamePadButton( { gamepad.dpad_up }).flow
    val dpadDown = GamePadButton( { gamepad.dpad_down }).flow
    val dpadLeft = GamePadButton( { gamepad.dpad_left }).flow
    val dpadRight = GamePadButton( { gamepad.dpad_right }).flow


    val rightBumper = GamePadButton( { gamepad.right_bumper }).flow
    val leftBumper = GamePadButton( { gamepad.left_bumper }).flow

    val rightTrigger = Trigger( { gamepad.right_trigger }).flow
    val leftTrigger = Trigger( { gamepad.left_trigger }).flow

    val rightJoystick = Joystick({ JoystickValues(gamepad.right_stick_x, gamepad.right_stick_y) }).flow
    val leftJoystick = Joystick({ JoystickValues(gamepad.right_stick_x, gamepad.right_stick_y) }).flow

    val rightJoystickButton = GamePadButton( { gamepad.right_stick_button }).flow





}

