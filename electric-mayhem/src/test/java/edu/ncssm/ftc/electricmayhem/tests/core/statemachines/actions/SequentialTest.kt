package edu.ncssm.ftc.electricmayhem.tests.core.statemachines.actions

import actions.Action
import actions.Sequential
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class, ExperimentalKotest::class)
class SequentialTest : DescribeSpec({
    coroutineTestScope = true

    describe("Sequential Actions")
    it("should complete all child actions in order") {
        var one = false
        var two = false
        var three = false

        val sequentialAction = Sequential(
            Action {
                delay(100)
                one = true
            },
            Action {
                delay(100)
                two = true
            },
            Action {
                delay(100)
                three = true
            }
        )

        val job = launch { sequentialAction.execute() }

        testCoroutineScheduler.advanceTimeBy(120)
        one shouldBe true
        two shouldBe false
        three shouldBe false


        testCoroutineScheduler.advanceTimeBy(100)
        one shouldBe true
        two shouldBe true
        three shouldBe false


        testCoroutineScheduler.advanceTimeBy(100)
        one shouldBe true
        two shouldBe true
        three shouldBe true

        job.isCompleted shouldBe  true
    }

})
