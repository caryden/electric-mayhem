package subsystems

import actions.SubsystemAction
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.equality.shouldNotBeEqualToComparingFieldsExcept
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class SubsystemTest : DescribeSpec({
    coroutineTestScope = true


    class testSubsystem(dispatcher: CoroutineDispatcher) :Subsystem(dispatcher) {
        public inner class TestSubSystemAction(val toExecute : suspend () -> Unit) : SubsystemAction(this){
            override suspend fun subsystemExecute() {
                toExecute()
            }
        }
    }



    describe("Subssytems") {
        it("a subsystem should be able to execute subsystem actions") {
            var executed = false
            val ss = testSubsystem(StandardTestDispatcher(testCoroutineScheduler))
            val ssAction = ss.TestSubSystemAction { executed = true }
            ssAction.execute()
            testCoroutineScheduler.advanceTimeBy(1000)
            executed shouldBe  true
        }
        it("should only allow one subsystem action at a time (executing a new one, cancels the current one)") {
            var executed1 = false
            var executed2 = false
            val ss = testSubsystem(StandardTestDispatcher(testCoroutineScheduler))
            val ssAction1 = ss.TestSubSystemAction {
                delay(100)
                executed1 = true
            }
            val ssAction2 = ss.TestSubSystemAction {
                executed2 = true
            }
            ssAction1.execute()
            testCoroutineScheduler.advanceTimeBy(10)

            ssAction2.execute() // should cancel ssAction1
            testCoroutineScheduler.advanceTimeBy(1000)
            executed1 shouldBe  false
            executed2 shouldBe true
        }
    }

})
