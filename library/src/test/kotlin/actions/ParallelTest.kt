package actions

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class, ExperimentalKotest::class)
class ParallelTest : DescribeSpec({
    coroutineTestScope = true

    describe("Parallel Actions")
        it("should complete all child actions") {
            var one = false
            var two = false
            var three = false

            val parallelAction = Parallel(
                Generic {
                    delay(100)
                    one = true
                },
                Generic {
                    delay(200)
                    two = true
                },
                Generic {
                    delay(300)
                    three = true
                }
            )

            val job = launch { parallelAction.execute() }

            testCoroutineScheduler.advanceTimeBy(150)
            one shouldBe true
            two shouldBe false
            three shouldBe false
            job.isCompleted shouldBe false

            testCoroutineScheduler.advanceTimeBy(100)
            one shouldBe true
            two shouldBe true
            three shouldBe false
            job.isCompleted shouldBe false

            testCoroutineScheduler.advanceTimeBy(100)
            one shouldBe true
            two shouldBe true
            three shouldBe true
            job.isCompleted shouldBe true
        }

})
