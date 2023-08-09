import kotlinx.coroutines.runBlocking
import sensors.GamePad
import sensors.Sensor
import sensors.goesActive
import statemachines.fsm

enum class States {
    INTAKE,
    TRAVEL,
    SCOREHIGH,
    SCOREMEDIUN,
    SCORELOW,
    SCOREGROUND,
    FALLENCONEPICKUP
}

fun main() = runBlocking {

    // sensors
    val driverGamePad = GamePad()
    //val gunnerGamePad = GamePad()

    // each sensor is set up with the polling time that makes sense of the sensor
    val beamBreak = Sensor({ false }, 10L)
    //val rangeSensor = Sensor({ 100 }, 50L)

    val robot = Robot()

    // Finite State Machine is defined with a DSL
    val stateMachine = fsm(States.TRAVEL) {
        transitions {
            whenIn(States.TRAVEL) {
                whenever(driverGamePad.x.goesActive()).transitionTo(States.SCOREHIGH).andDo(robot.turret.MoveToAngle(135.0))
                whenever(driverGamePad.x.goesActive()).transitionTo(States.SCOREMEDIUN).andDo(actions.Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(States.SCORELOW).andDo(actions.Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(States.SCOREGROUND).andDo(actions.Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(States.INTAKE).andDo(actions.Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(States.FALLENCONEPICKUP).andDo(actions.Action.NoAction)
            }
            whenIn(States.INTAKE) {
                whenever(driverGamePad.x.goesActive()).transitionTo(States.SCOREHIGH).andDo(actions.Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(States.SCOREMEDIUN).andDo(actions.Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(States.SCORELOW).andDo(actions.Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(States.SCOREGROUND).andDo(actions.Action.NoAction)
                whenever(beamBreak.flow.goesActive()).transitionTo(States.TRAVEL).andDo(actions.Action.NoAction)
                whenever(driverGamePad.x.goesActive()).transitionTo(States.FALLENCONEPICKUP).andDo(actions.Action.NoAction)
            }
            // etc.
        }
    }

    robot.shutdown()
    stateMachine.shutdown()
}
