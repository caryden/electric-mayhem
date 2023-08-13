package edu.ncssm.ftc.electric_mayhem.fsm.general

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class TestDispatcherTest : FunSpec() {
    init {
        test("advance time").config(coroutineTestScope = true) {
            val duration = 1.days
            // launch a coroutine that would normally sleep for 1 day
            launch {
                delay(duration.inWholeMilliseconds)
            }
            // move the clock on and the delay in the above coroutine will finish immediately.
            testCoroutineScheduler.advanceTimeBy(duration.inWholeMilliseconds)
//            val currentTime = testCoroutineScheduler.currentTime
        }
    }
}