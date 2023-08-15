package statemachines

import actions.Action
import edu.ncssm.ftc.electric_mayhem.core.sensors.data.SensorData
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import sensors.goesActive
import sensors.goesInactive
import sensors.not
import java.util.concurrent.atomic.AtomicBoolean

var testState1OnEnterActionRan = 0
val testState1OnEnterAction = object : Action({
    testState1OnEnterActionRan += 1
    println("initialActionRan")
}) {
}
var testState1OnExitActionRan = 0
val testState1OnExitAction = object : Action({
    testState1OnExitActionRan += 1
    println("initialActionRan")
}) {
}
sealed class TestStates(actionOnEnter : Action = Action.NoAction, actionOnExit : Action = Action.NoAction )
    : FiniteStateMachineStates(actionOnEnter, actionOnExit) {
    object TestState1 : TestStates(actionOnEnter = testState1OnEnterAction, actionOnExit = testState1OnExitAction )
    object TestState2 : TestStates()
    object TestState3 : TestStates()
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalKotest::class, ExperimentalStdlibApi::class)
class FiniteStateMachineTest : DescribeSpec({
    coroutineTestScope = true

    describe("A finite state machine") {

        it("should start in the initial state and action onenter should run") {
            testState1OnEnterActionRan = 0

            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            val fsm = fsm<TestStates>(TestStates.TestState1, testDispatcher) {}

            fsm.start()
            testCoroutineScheduler.advanceUntilIdle()
            fsm.currentState shouldBe TestStates.TestState1
            testState1OnEnterActionRan shouldBe 1

            fsm.shutdown()
        }
        it("A StateFlow should cause a trigger") {
            val sensorFlow = MutableStateFlow<Boolean>(false)
            val collected = AtomicBoolean(false)
            val job = launch {
                sensorFlow
                    .onEach { println("oneach $it") }
                    .map { SensorData(it) }
                    .goesActive()
                    .onEach {  } //needed for some strange reason only in the test fixture
                    .collect() {
                        println("collect $it")
                        collected.set(true)
                    }
            }
            sensorFlow.value = false
            testCoroutineScheduler.advanceTimeBy(1000)

            sensorFlow.value = true
            testCoroutineScheduler.advanceTimeBy(1000)
            collected.get() shouldBe true
            job.cancel()
        }

        it("should transition to the next state on trigger and run onexit and onenter actions for the States") {
            testState1OnEnterActionRan = 0
            testState1OnExitActionRan = 0;

            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            val sensor = MutableStateFlow(false)
            var movesToStateActionRan = 0
            val movesToStateAction = object : Action({
                movesToStateActionRan += 1
            }) { }


            val fsm = FiniteStateMachine<TestStates>(TestStates.TestState1, testDispatcher)
            fsm.transitions {
                whenIn(TestStates.TestState1) {
                    whenever(sensor).transitionTo(TestStates.TestState2).andDo(movesToStateAction)
                }
                whenIn(TestStates.TestState2) {
                    whenever(sensor.not()).transitionTo(TestStates.TestState1).andDo(movesToStateAction)
                }

            }

            fsm.start()

            testCoroutineScheduler.advanceTimeBy(1000)

            testState1OnEnterActionRan shouldBe 1 // entered the initial state at startup
            fsm.currentState shouldBe TestStates.TestState1

            sensor.value =  true
            testCoroutineScheduler.advanceTimeBy(1000)

            testState1OnExitActionRan shouldBe 1 //  exited the initial state on transition
            fsm.currentState shouldBe TestStates.TestState2
            movesToStateActionRan shouldBe 1

            sensor.value =  false
            testCoroutineScheduler.advanceTimeBy(1000)

            testState1OnEnterActionRan shouldBe 2 // re-entered the initial state
            fsm.currentState shouldBe TestStates.TestState1
            movesToStateActionRan shouldBe 2

            fsm.shutdown()
        }

        it("should throw an exception when adding a transition that already exists") {
            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            val fsm = FiniteStateMachine<TestStates>(TestStates.TestState1, testDispatcher)

            shouldThrow<IllegalArgumentException> {
                fsm.transitions {
                    whenIn(TestStates.TestState1) {
                        whenever(flowOf(true)).transitionTo(TestStates.TestState2).andDo(Action.NoAction)
                        whenever(flowOf(true)).transitionTo(TestStates.TestState2).andDo(Action.NoAction)
                    }
                }
            }
            fsm.shutdown()
        }
    }
})
