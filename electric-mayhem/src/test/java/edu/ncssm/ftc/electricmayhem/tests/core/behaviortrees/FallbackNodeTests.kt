package edu.ncssm.ftc.electricmayhem.tests.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.actions.ActionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.BehaviorTree
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.control.FallbackNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class FallbackNodeTests : DescribeSpec({
    coroutineTestScope = true
    describe("A behavior tree") {
        context("with a single Fallback Control Node") {
            it("should execute the fallback children actions until one returns success") {
                var testActionsRan  = 0
                val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
                val testFallback = FallbackNode(
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Failure
                    },
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Success
                    },
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Success
                    })
                val testBehaviorTree = BehaviorTree(testDispatcher, testFallback)
                testBehaviorTree.start()
                testCoroutineScheduler.advanceUntilIdle()
                testActionsRan shouldBe 2
                testFallback.status.value shouldBe NodeStatus.Success
            }
            it("should execute the fallback children actions and return failure if none return success") {
                var testActionsRan  = 0
                val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
                val testFallback = FallbackNode(
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Failure
                    },
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Failure
                    },
                    ActionNode {
                        testActionsRan += 1
                        NodeStatus.Failure
                    })
                val testBehaviorTree = BehaviorTree(testDispatcher, testFallback)
                testBehaviorTree.start()
                testCoroutineScheduler.advanceUntilIdle()
                testActionsRan shouldBe 3
                testFallback.status.value shouldBe NodeStatus.Failure
            }
        }
    }
})