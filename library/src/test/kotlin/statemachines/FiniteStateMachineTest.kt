package statemachines

import actions.Action
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import sensors.goesActive
import sensors.goesInactive
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalKotest::class, ExperimentalStdlibApi::class)
class FiniteStateMachineTest : DescribeSpec({
    coroutineTestScope = true

    describe("A finite state machine") {

        it("should start in the initial state and initial action should run") {
            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            var initialActionRan = false
            val initialAction = object : Action() {
                override suspend fun execute() {
                    initialActionRan = true
                    println("initialActionRan")
                }
            }

            val fsm = FiniteStateMachine(TestStates.STATE1, initialAction, testDispatcher)

            testCoroutineScheduler.advanceUntilIdle()
            fsm.currentState shouldBe TestStates.STATE1
            initialActionRan shouldBe true

            fsm.shutdown()
        }
        it("A StateFlow should cause a trigger") {
            val sensorFlow = MutableStateFlow<Boolean>(false)
            val collected = AtomicBoolean(false)
            val job = launch {
                sensorFlow
                    .onEach { println("oneach $it") }
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

        it("should transition to the next state on trigger") {
            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            val sensor = MutableStateFlow(false)
            var movesToStateActionRan = false
            val movesToStateAction = object : Action() {
                override suspend fun execute() {
                    movesToStateActionRan = !movesToStateActionRan
                    println("movesToStateActionRan")
                }
            }
            val fsm = FiniteStateMachine(TestStates.STATE1, Action.NoAction, testDispatcher)
            fsm.transitions {
                whenIn(TestStates.STATE1) {
                    whenever(sensor.goesActive()).transitionTo(TestStates.STATE2).andDo(movesToStateAction)
                }
                whenIn(TestStates.STATE2) {
                    whenever(sensor.goesInactive()).transitionTo(TestStates.STATE1).andDo(movesToStateAction)
                }
            }

            sensor.value = false
            testCoroutineScheduler.advanceTimeBy(1000)
            sensor.value =  true
            testCoroutineScheduler.advanceTimeBy(1000)

            fsm.currentState shouldBe TestStates.STATE2
            movesToStateActionRan shouldBe true

            sensor.value =  false
            testCoroutineScheduler.advanceTimeBy(1000)

            fsm.currentState shouldBe TestStates.STATE1
            movesToStateActionRan shouldBe false

            fsm.shutdown()
        }

        it("should throw an exception when adding a transition that already exists") {
            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            val fsm = FiniteStateMachine(TestStates.STATE1, Action.NoAction, testDispatcher)

            shouldThrow<IllegalArgumentException> {
                fsm.transitions {
                    whenIn(TestStates.STATE1) {
                        whenever(flowOf(true)).transitionTo(TestStates.STATE2).andDo(Action.NoAction)
                        whenever(flowOf(true)).transitionTo(TestStates.STATE2).andDo(Action.NoAction)
                    }
                }
            }
            fsm.shutdown()
        }
    }
})
enum class TestStates {
    STATE1,
    STATE2
}
