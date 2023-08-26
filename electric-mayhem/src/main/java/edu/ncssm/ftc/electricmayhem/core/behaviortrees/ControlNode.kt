package edu.ncssm.ftc.electricmayhem.core.behaviortrees

interface ControlNode : Node {
    val children: List<Node>
}