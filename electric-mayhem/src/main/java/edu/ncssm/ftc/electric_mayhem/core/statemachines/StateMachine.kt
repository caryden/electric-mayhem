package edu.ncssm.ftc.electric_mayhem.core.statemachines

import java.io.Closeable

interface StateMachine : Closeable {
    companion object {
        val NoStateMachine =  object :
            StateMachine {  override fun start() { } override fun close() { } }
    }
    fun start()
}