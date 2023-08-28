package edu.ncssm.ftc.electricmayhem.tests.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.actions.ActionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.BehaviorTree
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalStdlibApi::class)
class ActionNodeTests : DescribeSpec({
    coroutineTestScope = true
    describe("A behavior tree") {
        context("with a single action node") {
            it("should start and execute that single action and return success") {
                var testActionRan = false
                val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
                val testAction = ActionNode {
                    testActionRan = true
                    NodeStatus.Success
                }
                val testBehaviorTree = BehaviorTree(testDispatcher, testAction)
                testBehaviorTree.start()
                testCoroutineScheduler.advanceUntilIdle()
                testActionRan shouldBe true
                testBehaviorTree.root.status.value shouldBe NodeStatus.Success
            }
        }
    }
})
