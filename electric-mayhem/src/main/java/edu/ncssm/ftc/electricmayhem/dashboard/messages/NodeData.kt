package edu.ncssm.ftc.electricmayhem.dashboard.messages

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.actions.ActionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.conditions.ConditionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.control.ControlNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators.DecoratorNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import kotlinx.serialization.Serializable

@Serializable
data class NodeData(val id: String, val nodeType : String, val name : String,  val status: NodeStatus, val executionTimeMs : Long, val description: String, val parentId : String?) {
    constructor(node: ActionNode, parentId : String?) : this(getNextId(), "action", node.name, node.status.value, node.executionTimeMs, node.description, parentId)
    constructor(node: ControlNode, parentId : String?) : this(getNextId(), "control", node.name, node.status.value, node.executionTimeMs, node.description, parentId)
    constructor(node: ConditionNode, parentId : String?) : this(getNextId(), "condition",node.name, node.status.value, node.executionTimeMs, node.description, parentId)
    constructor(node: DecoratorNode, parentId : String?) : this(getNextId(), "decorator",node.name, node.status.value, node.executionTimeMs, node.description, parentId)
    companion object {
        private var nextId = 0
        private fun getNextId() = "node-${nextId++}"
    }

}
