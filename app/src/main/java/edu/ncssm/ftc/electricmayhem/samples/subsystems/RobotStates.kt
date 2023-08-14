package edu.ncssm.ftc.electricmayhem.samples.subsystems

import actions.Action
import statemachines.FiniteStateMachineStates

sealed class RobotStates(actionOnEnter : Action = Action.NoAction, actionOnExit : Action = Action.NoAction )
    : FiniteStateMachineStates(actionOnEnter, actionOnExit) {
    object Intake : RobotStates()
    object Travel : RobotStates(actionOnEnter = Action.NoAction, actionOnExit = Action.NoAction)
    object ScoreHighJunction : RobotStates()
    object ScoreMediumJunction : RobotStates()
    object ScoreLowJunction : RobotStates()
    object ScoreGroundJunction : RobotStates()
    object PickupFallenCone : RobotStates()
}