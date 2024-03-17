package edu.ncssm.ftc.electricmayhem.dashboard.commands

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.BehaviorTree
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.actions.ActionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.conditions.ConditionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.control.ControlNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators.DecoratorNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.dashboard.messages.NodeData
import kotlinx.serialization.Serializable

@Serializable
data class InitializeBehaviorTree(val name : String, val nodes : List<NodeData>) {
    constructor(bt: BehaviorTree) : this(bt.toString(), getNodeData(bt))
    companion object {
        fun getNodeData(bt: BehaviorTree) : List<NodeData> {
            val nodeList = mutableListOf<NodeData>()

            // depth first search
            fun dfs(node: Node, parentId: String? = null) {
                when (node) {
                    is ConditionNode -> {
                        nodeList.add(NodeData(node, parentId))
                    }
                    is ActionNode -> {
                        nodeList.add(NodeData(node, parentId))
                    }
                    is ControlNode -> {
                        val parentNodeData = NodeData(node, parentId)
                        nodeList.add(parentNodeData)
                        // Add the children to the list
                        for (child in node.children) {
                            dfs(child, parentNodeData.id)
                        }
                    }
                    is DecoratorNode -> {
                        val parentNodeData = NodeData(node, parentId)
                        nodeList.add(parentNodeData)
                        // Add the single child to the list
                        dfs(node.child, parentNodeData.id)
                    }
                }
            }

            dfs(bt.root)
            return nodeList
        }
    }
}
