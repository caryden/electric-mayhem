package actions

import android.util.Log
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class ActionTest : DescribeSpec({
    coroutineTestScope = true

    describe("Actions") {
        it("should timeout and cancel if they exceed the timeout") {
            val timeOutMs = 100L
            var completed = false
            var calledOnTimeout = false
            val actionThatWillTimeout = Action {
                delay(2 * timeOutMs)
                completed = true
            }.withTimeout(timeOutMs).onTimeout { calledOnTimeout = true }

            val job = launch { actionThatWillTimeout.execute() }
            testCoroutineScheduler.advanceTimeBy(timeOutMs + 1L)
            job.isCompleted shouldBe true
            completed shouldBe false
            calledOnTimeout shouldBe true
        }
    }
})
