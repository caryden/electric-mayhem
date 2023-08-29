package edu.ncssm.ftc.electricmayhem.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.conditions.ConditionChangedEvent
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.conditions.ConditionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.control.ControlNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators.DecoratorNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.Node
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.TickContext
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

        // Unlike a normal behavior tree, we don't have a loop that ticks the tree over and over on a timer.
        // Here we just wait for a condition to change.  When it does, we tick the tree.
        // Also note that the ActionNodes do not launch their own coroutines and then return RUNNING quickly and then update
        // their status on the next tick() call.  Instead, they just run their code and return SUCCESS or FAILURE.  Each tick is
        // launched in its own coroutine.  This allows us to cancel the prior tick job if a condition changes.  In a normal
        // behavior tree, we have to seek out and cancel and RUNNING nodes.  Here, we just cancel the current tick job.
        treeScope.launch {
            var currentTickJob: Job? = null
            var tickCount : Long = 0

            // here I create a new coroutine scope for the tickTrigger flow.  This is because I want to be able to cancel
            // the tick job when a condition changes.  I don't want to cancel the entire treeScope because that would cancel
            // the tickTrigger flow and we would never tick again.
            val tickScope = CoroutineScope(treeScope.coroutineContext + Job())
            tickTrigger.collectLatest { cc ->
                if (cc.newStatus in setOf(NodeStatus.Success, NodeStatus.Failure)) {
                    // wait for the current tick job (if one exists) to cancel and completely finish to avoid race conditions
                    currentTickJob?.cancelAndJoin()

                    val tickContext = TickContext(tickCount++)
                    currentTickJob = tickScope.launch {
                        root.tick(tickContext)
                    }
                }
            }
        }

        // This is the part of the code that makes it really reactive.  We scan the tree for condtion nodes.
        // The we collect the status of each node. Becuase the status is a flow, when it changes, it will emit
        // its new value for us ot collect.  Then, we emit a ConditionChangedEvent to the tickTrigger flow.
        // This will cause the tree to tick again.
        conditions.forEach {condition ->
            treeScope.launch {
                condition.status.collectLatest {
                    tickTrigger.value = ConditionChangedEvent(condition, condition.status.value)
                }
            }
        }

        // If we have no conditions, just run one tick of the root node
        // Not really sure what to do here.  If there are no conditions, the tree will never tick.
        // So why even have a behavior tree?  It is basically a procedure call at that point.
        // So that is what we do.  We just run the root node once.
        if(conditions.isEmpty()) {
            treeScope.launch {
                root.tick(TickContext(0))
            }
        }
    }
    override fun close() {
        treeScope.cancel()
        conditions.forEach { it.close() }
    }
}