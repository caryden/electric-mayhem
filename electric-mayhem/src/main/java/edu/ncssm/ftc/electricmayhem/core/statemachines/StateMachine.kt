package edu.ncssm.ftc.electricmayhem.core.statemachines

import edu.ncssm.ftc.electricmayhem.core.general.BehaviorController
import java.io.Closeable

interface StateMachine : BehaviorController, Closeable {
    companion object {
        val NoStateMachine =  object :
            StateMachine {  override fun start() { } override fun close() { } }
    }
}