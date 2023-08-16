package edu.ncssm.ftc.electricmayhem.tests.core.sensors

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import edu.ncssm.ftc.electricmayhem.core.sensors.gamepad.GamePadButton
import sensors.goesActive
import sensors.goesInactive


@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class GamePadButtonTest : DescribeSpec({
    val pollingMs = 100L
    coroutineTestScope = true

    describe("edu.ncssm.ftc.electricmayhem.core.sensors.gamepad.GamePadButton") {
        context("when button is pressed") {
            it("should be registered as pressed") {
                var timesPressed = 0
                var simulatedButtonState = false
                val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
                val gamePadButton = GamePadButton({ simulatedButtonState }, testDispatcher )
                val collectorJob = launch {
                    gamePadButton.goesActive().collectLatest {
                        timesPressed++
                    }
                }
                testCoroutineScheduler.advanceTimeBy(2* pollingMs) // advance time to let the flow emit
                simulatedButtonState = true
                testCoroutineScheduler.advanceTimeBy(2* pollingMs) // advance time to let the flow emit
                simulatedButtonState = false
                testCoroutineScheduler.advanceTimeBy(2 * pollingMs) // advance time to let the flow emit
                timesPressed shouldBe 1
                collectorJob.cancel()
                gamePadButton.close()
            }
        }

        context("when button is released") {
            it("should be registered as released") {
                var timesReleased = 0
                var simulatedButtonState = false
                val testDispatcher = kotlin.coroutines.coroutineContext[CoroutineDispatcher.Key]!!
                val gamePadButton = GamePadButton({ simulatedButtonState }, testDispatcher)
                val collectorJob = launch {
                    gamePadButton
                        .goesInactive()
                        .collectLatest {
                        timesReleased++
                    }
                }
                testCoroutineScheduler.advanceTimeBy(2* pollingMs) // advance time to let the flow emit
                simulatedButtonState = true
                testCoroutineScheduler.advanceTimeBy(2 * pollingMs) // advance time to let the flow emit
                simulatedButtonState = false
                testCoroutineScheduler.advanceTimeBy(2 * pollingMs) // advance time to let the flow emit
                timesReleased shouldBe 1
                collectorJob.cancel()
                gamePadButton.close()
            }
        }
    }

})
