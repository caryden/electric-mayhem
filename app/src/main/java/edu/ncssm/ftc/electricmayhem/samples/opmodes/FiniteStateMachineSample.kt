package edu.ncssm.ftc.electricmayhem.samples.opmodes

import actions.Action
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import edu.ncssm.ftc.electricmayhem.samples.subsystems.Robot
import edu.ncssm.ftc.electricmayhem.samples.subsystems.RobotStates
import sensors.GamePadFlow
import sensors.SensorFlow
import sensors.goesActive
import statemachines.fsm

class FiniteStateMachineSample : LinearOpMode() {
    override fun runOpMode() {
        val robot = Robot(hardwareMap, gamepad1, gamepad2)

        waitForStart()

        robot.use {
            robot.start()
            while (opModeIsActive()) {
                telemetry.addData("State", robot.currentState)
                telemetry.update()
            }
        }
    }
}