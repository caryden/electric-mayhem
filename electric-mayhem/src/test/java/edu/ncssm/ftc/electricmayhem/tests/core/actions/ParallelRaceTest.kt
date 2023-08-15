package edu.ncssm.ftc.electricmayhem.tests.core.actions

import actions.Action
import actions.ParallelRace
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class, ExperimentalKotest::class)
class ParallelRaceTest : DescribeSpec({
    coroutineTestScope = true

    describe("Parallel Race Condition Actions")
    it("should complete when the first child action complete and the other should be cancelled") {
        var one = false
        var two = false
        var three = false

        val parallelRaceAction = ParallelRace(
            Action {
                delay(100)
                one = true
            },
            Action {
                delay(200)
                two = true
            },
            Action {
                delay(300)
                three = true
            }
        )

        launch { parallelRaceAction.execute() }

        testCoroutineScheduler.advanceTimeBy(1000)
        one shouldBe true
        two shouldBe false
        three shouldBe false
    }

})
