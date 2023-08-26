package edu.ncssm.ftc.electricmayhem.core.behaviortrees

interface DecoratorNode : Node {
    val child: Node
}