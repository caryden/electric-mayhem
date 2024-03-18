package edu.ncssm.ftc.electricmayhem.tests.core.general

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.*
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class TestDispatcherTest : FunSpec() {
    init {
        test("advance time").config(coroutineTestScope = true) {
            val duration = 10.milliseconds
            var done = false
            // launch a coroutine that would normally sleep for 1 day
            launch {
                delay(duration.inWholeMilliseconds)
                println("done")
                done = true
            }
            done shouldBe false
            // move the clock on and the delay in the above coroutine will finish immediately.
            testCoroutineScheduler.advanceTimeBy(duration.inWholeMilliseconds + 10)
            done shouldBe true
        }
    }
}