package edu.ncssm.ftc.electricmayhem.core.statemachines

import java.io.Closeable

interface StateMachine : Closeable {
    companion object {
        val NoStateMachine =  object :
            StateMachine {  override fun start() { } override fun close() { } }
    }
    fun start()
}