package edu.ncssm.ftc.electricmayhem.core.behaviortrees.control

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node

abstract class ControlNode : Node() {
    abstract val children: List<Node>
}