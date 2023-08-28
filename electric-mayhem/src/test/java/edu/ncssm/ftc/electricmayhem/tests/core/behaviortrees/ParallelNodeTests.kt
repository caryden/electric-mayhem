package edu.ncssm.ftc.electricmayhem.tests.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.actions.ActionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.BehaviorTree
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.control.ParallelNode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class ParallelNodeTests : DescribeSpec({
    coroutineTestScope = true
    describe("A behavior tree") {
        context("with a single Parallel Control Node") {
            it("should execute the all children actions until and returns failure if any one fails") {
                var testActionsRan  = 0
                val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
                val testParallel = ParallelNode (
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Success
                    },
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Success
                    },
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Failure
                    })
                val testBehaviorTree = BehaviorTree(testDispatcher, testParallel)
                testBehaviorTree.start()
                testCoroutineScheduler.advanceUntilIdle()
                testActionsRan shouldBe 3
                testParallel.status.value shouldBe NodeStatus.Failure
            }
            it("should execute the children actions and return success if all return success") {
                var testActionsRan  = 0
                val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
                val testParallel = ParallelNode(
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Success
                    },
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Success
                    },
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Success
                    })
                val testBehaviorTree = BehaviorTree(testDispatcher, testParallel)
                testBehaviorTree.start()
                testCoroutineScheduler.advanceUntilIdle()
                testActionsRan shouldBe 3
                testParallel.status.value shouldBe NodeStatus.Success
            }
        }
    }
})