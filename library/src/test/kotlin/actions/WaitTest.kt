package actions

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalKotest::class, ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class WaitTest : DescribeSpec({
    coroutineTestScope = true

    describe("wait actions")
        it("should not complete before the designated wait") {
            val waitMs = 100L
            val wait = Wait(waitMs)
            val job = launch { wait.execute() }
            testCoroutineScheduler.advanceTimeBy(waitMs - 1L)
            job.isCompleted shouldBe false
            job.cancel()
        }
    it("should complete after the designated wait") {
        val waitMs = 100L
        val wait = Wait(waitMs)
        val job = launch { wait.execute() }
        testCoroutineScheduler.advanceTimeBy(waitMs + 1L)
        job.isCompleted shouldBe true
        job.cancel()
    }

})
