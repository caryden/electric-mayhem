package edu.ncssm.ftc.electricmayhem.core.general

import subsystems.Subsystem

abstract class RobotBase : Subsystem() {
    abstract val behaviorController : BehaviorController
}