package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.general.BehaviorController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable

class BehaviorTree(dispatcher: CoroutineDispatcher = Dispatchers.Default, val root : Node) : BehaviorController, Closeable {
    private val treeScope = CoroutineScope(dispatcher)
    val conditions = mutableListOf<ConditionNode>()
    private val tickTrigger = MutableStateFlow(ConditionChangedEvent(null, NodeStatus.Idle))
    init {
        conditions.addAll(getConditionNodes(root))
    }

    private fun getConditionNodes(node : Node): List<ConditionNode> {
        val conditions = mutableListOf<ConditionNode>()
        when (node) {
            is ConditionNode -> conditions.add(node)
            is ControlNode -> {
                for (child in node.children)
                    conditions.addAll(getConditionNodes(child))
            }
            is DecoratorNode -> {
                conditions.addAll(getConditionNodes(node.child))
            }
        }
        return conditions
    }

    override fun start() {
        treeScope.launch {
            var currentTickJob: Job? = null
            tickTrigger.collectLatest {
                if (it.newStatus in setOf(NodeStatus.Success, NodeStatus.Failure)) {
                    // wait for the current tick job (if one exists) to cancel and completely finish to avoid race conditions
                    currentTickJob?.cancelAndJoin()
                    currentTickJob = treeScope.launch {
                        root.tick()
                    }
                }
            }
        }
        conditions.forEach {condition ->
            treeScope.launch {
                condition.status.collectLatest {
                    tickTrigger.value = ConditionChangedEvent(condition, condition.status.value)
                }
            }
        }

        // if we have no conditions, just run one tick of the root node
        if(conditions.isEmpty()) {
            treeScope.launch {
                root.tick()
            }
        }
    }
    override fun close() {
        treeScope.cancel()
        conditions.forEach { it.close() }
    }
}