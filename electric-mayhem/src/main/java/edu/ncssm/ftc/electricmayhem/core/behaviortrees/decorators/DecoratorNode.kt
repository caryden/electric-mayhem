package edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node

interface DecoratorNode : Node {
    val child: Node
}