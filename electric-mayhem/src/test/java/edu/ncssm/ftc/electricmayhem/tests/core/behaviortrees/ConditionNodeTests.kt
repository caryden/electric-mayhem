package edu.ncssm.ftc.electricmayhem.tests.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.ActionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.BehaviorTree
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.ConditionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.FallbackNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.NodeStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class ConditionNodeTests: DescribeSpec({
    coroutineTestScope = true
    context("with condition then an action") {

        it("should not execute the action if the condition is true, status should be success") {
            var testActionsRan  = 0
            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            val testFallback = FallbackNode(
                ConditionNode(MutableStateFlow(true), true, testDispatcher),
                ActionNode {
                    testActionsRan += 1
                    NodeStatus.Failure
                })
            val testBehaviorTree = BehaviorTree(testDispatcher, testFallback)
            testBehaviorTree.start()
            testCoroutineScheduler.advanceUntilIdle()
            testActionsRan shouldBe 0
            testFallback.status.value shouldBe NodeStatus.Success
        }
        it("should execute the action if the condition is false, status should be success") {
            var testActionsRan  = 0
            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            val testFallback = FallbackNode(
                ConditionNode(flowOf(false), false, testDispatcher),
                ActionNode {
                    testActionsRan += 1
                    NodeStatus.Success
                })
            val testBehaviorTree = BehaviorTree(testDispatcher, testFallback)
            testBehaviorTree.start()
            testCoroutineScheduler.advanceTimeBy(100)
            testActionsRan shouldBe 1
            testFallback.status.value shouldBe NodeStatus.Success
        }
        it("should trigger a tick() and run the action when the condition changes, status should be success") {
            var testActionsRan  = 0
            val testFlow = MutableStateFlow(true)
            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            val testFallback = FallbackNode(
                ConditionNode(testFlow, testFlow.value, testDispatcher),
                ActionNode {
                    testActionsRan += 1
                    NodeStatus.Success
                })
            val testBehaviorTree = BehaviorTree(testDispatcher, testFallback)
            testBehaviorTree.start()
            testCoroutineScheduler.advanceTimeBy(100)
            testActionsRan shouldBe 0
            testFallback.status.value shouldBe NodeStatus.Success

            testFlow.value = false // this should trigger another tick()
            testCoroutineScheduler.advanceTimeBy(1000)
            testActionsRan shouldBe 1
            testFallback.status.value shouldBe NodeStatus.Success
        }
    }
})