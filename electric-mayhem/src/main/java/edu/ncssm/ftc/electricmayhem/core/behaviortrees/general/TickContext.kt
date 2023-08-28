package edu.ncssm.ftc.electricmayhem.core.behaviortrees.general

class TickContext(val tickCount : Long) {
    val startTime = System.nanoTime()
    val tickedNodes = mutableListOf<Node>()
}