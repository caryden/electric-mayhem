package edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node

abstract class DecoratorNode : Node() {
   abstract val child: Node
}