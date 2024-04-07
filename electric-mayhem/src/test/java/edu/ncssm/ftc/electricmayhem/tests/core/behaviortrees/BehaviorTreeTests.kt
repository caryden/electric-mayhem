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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import edu.ncssm.ftc.electricmayhem.core.sensors.SensorFlow
import io.kotest.core.config.configuration

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class BehaviorTreeTests : DescribeSpec({
    coroutineTestScope = true
    describe("A few behavior tree examples written as tests to offend the pedantic") {
        context("Ben's Example from chatGPT") {
            // https://chat.openai.com/share/90be9512-a51b-416d-aede-4af291895c9f
            it("should start and execute the tree and return success") {
                val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)

                val isObstacleNearby = MutableStateFlow(false)
                var sensorValue = false

                // Here I am using a SensorFlow to simulate a sensor (for completeness really, you could any Flow<Boolean> here)
                // Because the sensorFlow has a coroutine that runs within its own scope, we have to close it when we are done
                // Because it implements Closeable, we can use the use() function to do this for us.  When it leaves scope, its
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

                        // Now this will demonstrate the reactive nature of the tree
                        // changing this meters will cause the sensor flow to emit a new meters
                        // which will cause the condition node to change its status
                        // which will cause the tree to reevaluate to 're-tick()'
                        // many BTs just have a tick() function that is called on a regular interval
                        // this forces long running actions to be broken up into smaller chunks and even spin up
                        // their own coroutines to do the work and return Running as a status.  This actually makes them
                        // more complicated than they need to be.  Here, we just run the action and return Success or Failure.
                        // Each tick is launched in its own coroutine.  This allows us to cancel the prior tick job if a condition changes.
                        // Which is precisely what other loop/time-slice BTs do, but they have to seek out and cancel each running node.
                        // Here, we just cancel the current tick job and create a new one.  This is much simpler.
                        // Look in the BehaviorTree class for the code that does this.
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
