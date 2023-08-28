package edu.ncssm.ftc.electricmayhem.core.behaviortrees.control

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node

interface ControlNode : Node {
    val children: List<Node>
}