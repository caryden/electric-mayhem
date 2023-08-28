package edu.ncssm.ftc.electricmayhem.tests.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.actions.ActionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.decorators.*
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*

@OptIn(ExperimentalStdlibApi::class)
class DecoratorNodeTests : DescribeSpec({
    coroutineTestScope = true

    describe("Decorator Nodes") {
        context("Retry Nodes") {
            it("should retry an action N times before failing") {
                var testActionRan = 0
                val testAction = ActionNode {
                    testActionRan += 1
                    NodeStatus.Failure
                }
                val tickContext = TickContext(0L)
                val testRetryNode = Retry(testAction, 3)
                val status  = testRetryNode.tick(tickContext)

                status shouldBe NodeStatus.Failure
                testActionRan shouldBe 3
            }
            it("should succeed as soon as the child succeeds") {
                var testActionRan = 0
                val testAction = ActionNode {
                    testActionRan += 1
                    if(testActionRan == 2)
                        NodeStatus.Success
                    else
                        NodeStatus.Failure
                }
                val tickContext = TickContext(0L)
                val testRetryNode = Retry(testAction, 3)
                val status  = testRetryNode.tick(tickContext)

                status shouldBe NodeStatus.Success
                testActionRan shouldBe 2
            }
        }
        context("RepeatUntil Nodes"){
            it("should repeat until the child returns the specified status") {
                var testActionRan = 0
                val testAction = ActionNode {
                    testActionRan += 1
                    if(testActionRan == 2)
                        NodeStatus.Success
                    else
                        NodeStatus.Failure
                }
                val tickContext = TickContext(0L)
                val testRepeatUntilNode = RepeatUntil(testAction, NodeStatus.Success)
                val status  = testRepeatUntilNode.tick(tickContext)

                status shouldBe NodeStatus.Success
                testActionRan shouldBe 2
            }

        }
        context("InvertStatus Nodes") {
            it("should invert the returned status" ){
                val testAction = ActionNode {
                    NodeStatus.Failure
                }
                val tickContext = TickContext(0L)
                val testInvertStatusNode = InvertStatus(testAction)
                val status  = testInvertStatusNode.tick(tickContext)

                status shouldBe NodeStatus.Success
            }
        }
        context("Cooldown Nodes") {
            it("should not run the child until the cooldown has expired") {
                var testActionRan = 0
                val testAction = ActionNode {
                    testActionRan += 1
                    NodeStatus.Success
                }
                val tickContext = TickContext(0L)
                val testCooldownNode = Cooldown(testAction, 1000)

                val status  = testCooldownNode.tick(tickContext)
                status shouldBe NodeStatus.Success
                testActionRan shouldBe 1

                val status2  = async { testCooldownNode.tick(tickContext)}
                testCoroutineScheduler.advanceTimeBy(500)
                testCooldownNode.status.value shouldBe NodeStatus.Running
                testCoroutineScheduler.advanceUntilIdle()
                status2.await() shouldBe NodeStatus.Success
            }
        }
        context("Timeout Nodes") {
            it("should timeout if action exceeds the time limit and cancel the action") {
                var testActionRan = 0
                var testActionCancelled = 0
                val testAction = ActionNode {
                    try {
                        testActionRan += 1
                        delay(1000)
                        NodeStatus.Success

                    } catch (e: CancellationException) {
                        testActionCancelled += 1
                        NodeStatus.Cancelled
                    }
                }
                val tickContext = TickContext(0L)
                val testTimeoutNode = Timeout(testAction, 100)

                val status  = testTimeoutNode.tick(tickContext)
                status shouldBe NodeStatus.Failure
                testActionCancelled shouldBe 1
                testActionRan shouldBe 1
            }
            it("should not timeout if action does not exceed the time limit and return Success") {
                var testActionRan = 0
                var testActionCancelled = 0
                val testAction = ActionNode {
                    try {
                        testActionRan += 1
                        delay(10)
                        NodeStatus.Success

                    } catch (e: CancellationException) {
                        testActionCancelled += 1
                        NodeStatus.Cancelled
                    }
                }
                val tickContext = TickContext(0L)
                val testTimeoutNode = Timeout(testAction, 100)

                val status  = testTimeoutNode.tick(tickContext)
                status shouldBe NodeStatus.Success
                testActionCancelled shouldBe 0
                testActionRan shouldBe 1
            }
        }
    }
})
