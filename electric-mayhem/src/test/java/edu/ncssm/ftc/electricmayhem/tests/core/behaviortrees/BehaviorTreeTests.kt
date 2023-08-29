package edu.ncssm.ftc.electricmayhem.tests.core.behaviortrees

import edu.ncssm.ftc.electricmayhem.core.behaviortrees.BehaviorTree
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.actions.ActionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.conditions.ConditionNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.control.FallbackNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.control.SequenceNode
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import sensors.SensorFlow

@OptIn(ExperimentalStdlibApi::class)
class BehaviorTreeTests : DescribeSpec({
    coroutineTestScope = true
    describe("A few behavior tree examples written as tests to offend the pedantic") {
        context("Ben's Example from chaGPT") {
            // https://chat.openai.com/share/90be9512-a51b-416d-aede-4af291895c9f
            it("should start and execute the tree and return success") {
                val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)

                val isObstacleNearby = MutableStateFlow(false)
                var sensorValue = false

                // Here I am using a SensroFlow to simulate a sensor (for completeness really, you could any Flow<Boolean> here)
                // Because the sensorFlow has a coroutine that runs within its own scope, we have to close it when we are done
                // Becuase it implements Closeable, we can use the use() function to do this for us.  When it leaves scope, its
                // close() function will be called and the coroutine will be cancelled
                SensorFlow( { sensorValue }, 100L, testDispatcher).use{ isObstacleNearbySensor ->
                    val isObstacleNearbyCondition = ConditionNode(isObstacleNearbySensor,false, testDispatcher)

                    val stopMovingAction = ActionNode {
                        println("Stopping")
                        NodeStatus.Success
                    }
                    val moveForwardAction = ActionNode {
                        println("Moving Forward")
                        NodeStatus.Success
                    }
                    val selector = FallbackNode(
                        SequenceNode(isObstacleNearbyCondition, stopMovingAction),
                        moveForwardAction)

                    // behavior tree is also a closeable, so we can use the use() function to close it when we are done
                    BehaviorTree(testDispatcher, selector).use{ bt ->
                        bt.start()

                        testCoroutineScheduler.advanceTimeBy(1000L)
                        isObstacleNearbyCondition.status.value shouldBe NodeStatus.Failure
                        moveForwardAction.status.value shouldBe NodeStatus.Success
                        bt.root.status.value shouldBe NodeStatus.Success

                        // Now this will demonstrate the reacitve nature of the tree
                        // changing this value will cause the sensor flow to emit a new value
                        // which will cause the condition node to change its status
                        // which will cause the tree to reevaluate 're-tick()'
                        sensorValue = true
                        testCoroutineScheduler.advanceTimeBy(1000L)
                        isObstacleNearbyCondition.status.value shouldBe NodeStatus.Success
                        stopMovingAction.status.value shouldBe NodeStatus.Success
                        bt.root.status.value shouldBe NodeStatus.Success
                    }
                }
            }
        }
    }
})
