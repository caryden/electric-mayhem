import actions.Action
import kotlinx.coroutines.runBlocking
import sensors.GamePad
import sensors.Sensor
import sensors.goesActive
import statemachines.fsm

fun main() = runBlocking {

    // sensors
    val driverGamePad = GamePad()
    //val gunnerGamePad = GamePad()

    // each sensor is set up with the polling time that makes sense of the sensor
    val beamBreak = Sensor({ false }, 10L)
    //val rangeSensor = Sensor({ 100 }, 50L)

    val robot = Robot()

    // Finite State Machine is defined with a DSL
    val stateMachine = fsm<RobotStates>(RobotStates.Travel) {
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
                whenever(beamBreak.flow.goesActive()).transitionTo(RobotStates.Travel).andDo(Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(RobotStates.PickupFallenCone).andDo(Action.NoAction)
            }
            // etc.
        }
    }

    robot.shutdown()
    stateMachine.shutdown()
}
