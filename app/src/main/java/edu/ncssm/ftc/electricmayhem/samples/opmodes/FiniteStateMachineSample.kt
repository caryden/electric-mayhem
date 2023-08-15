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
        val driverGamePad = GamePadFlow(gamepad1)
        val gunnerGamePad = GamePadFlow(gamepad2)
        val beamBreak = SensorFlow({ false }, 10L)

        val robot = Robot(hardwareMap)
        robot.stateMachine = fsm<RobotStates>(RobotStates.Travel) {
            transitions {
                whenIn(RobotStates.Travel) {
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.ScoreHighJunction).andDo(robot.turret.MoveToAngle(135.0))
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.ScoreMediumJunction).andDo(Action.NoAction)
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.ScoreLowJunction).andDo(Action.NoAction)
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.ScoreGroundJunction).andDo(Action.NoAction)
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.Intake).andDo(Action.NoAction)
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.PickupFallenCone).andDo(Action.NoAction)
                }
                whenIn(RobotStates.Intake) {
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.ScoreHighJunction).andDo(robot.turret.MoveToAngle(135.0))
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.ScoreMediumJunction).andDo(Action.NoAction)
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.ScoreLowJunction).andDo(Action.NoAction)
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.ScoreGroundJunction).andDo(Action.NoAction)
                    whenever(beamBreak.goesActive()).transitionTo(RobotStates.Travel).andDo(Action.NoAction)
                    whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.PickupFallenCone).andDo(Action.NoAction)
                }
                // etc.
            }
        }
        waitForStart()
        robot.use {
            robot.start()
            while (opModeIsActive()) {
                telemetry.addData("State", robot.stateMachine)
                telemetry.update()
            }
        }
    }
}