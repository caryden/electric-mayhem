package edu.ncssm.ftc.electricmayhem.samples.subsystems

import actions.Action
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import edu.ncssm.ftc.electricmayhem.core.sensors.gamepad.GamePadFlow
import kotlinx.coroutines.flow.map
import sensors.SensorFlow
import sensors.goesActive
import sensors.window
import statemachines.fsm
import subsystems.Subsystem

class Robot(hardwareMap: HardwareMap, gamepad1: Gamepad, gamepad2 : Gamepad) : Subsystem() {
    val turret = Turret(hardwareMap, "turretMotor")
    val beamBreak = SensorFlow({ false }, 10L)
    val batteryVoltage = SensorFlow({ hardwareMap.voltageSensor.first().voltage }, 100L)
    val driverGamePad = GamePadFlow(gamepad1)
    val gunnerGamePad = GamePadFlow(gamepad2)
    override val stateMachine = fsm<RobotStates>(RobotStates.Travel) {
        transitions {
            whenIn(RobotStates.Travel) {
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.ScoreHighJunction)
                    .andDo(turret.MoveToAngle(135.0))
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.ScoreMediumJunction)
                    .andDo(Action.NoAction)
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.ScoreLowJunction)
                    .andDo(Action.NoAction)
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.ScoreGroundJunction)
                    .andDo(Action.NoAction)
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.Intake)
                    .andDo(Action.NoAction)
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.PickupFallenCone)
                    .andDo(Action.NoAction)
            }
            whenIn(RobotStates.Intake) {
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.ScoreHighJunction)
                    .andDo(turret.MoveToAngle(135.0))
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.ScoreMediumJunction)
                    .andDo(Action.NoAction)
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.ScoreLowJunction)
                    .andDo(Action.NoAction)
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.ScoreGroundJunction)
                    .andDo(Action.NoAction)
                whenever(beamBreak.goesActive())
                    .transitionTo(RobotStates.Travel)
                    .andDo(Action.NoAction)
                whenever(driverGamePad.x.goesActive())
                    .transitionTo(RobotStates.PickupFallenCone)
                    .andDo(Action.NoAction)
            }
            // etc.
        }
    }

    init {
        hardwareMap.getAll(LynxModule::class.java).forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.AUTO }
        subsystems.add(turret)
        sensors.addAll(listOf(beamBreak, batteryVoltage))
    }
}



