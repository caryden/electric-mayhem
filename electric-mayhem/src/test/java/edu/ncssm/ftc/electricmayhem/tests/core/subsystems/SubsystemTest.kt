package edu.ncssm.ftc.electricmayhem.tests.core.subsystems

import actions.Parallel
import actions.Sequential
import edu.ncssm.ftc.electricmayhem.core.behaviortrees.general.NodeStatus
import edu.ncssm.ftc.electricmayhem.core.subsystems.SubsystemCommand
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import subsystems.Subsystem

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class SubsystemTest : DescribeSpec({
    coroutineTestScope = true
    class testSubsystem(dispatcher: CoroutineDispatcher) : Subsystem(dispatcher) {
        inner class TestSubSystemCommand(val toExecute : suspend () -> Unit) : SubsystemCommand(this){
            override suspend fun action() : NodeStatus {
                toExecute()
                return NodeStatus.Success
            }
        }
    }



    describe("Subsystems") {
        it("a subsystem should be able to start subsystem actions") {
            var executed = false
            val ss = testSubsystem(StandardTestDispatcher(testCoroutineScheduler))
            val ssAction = ss.TestSubSystemCommand { executed = true }
            ssAction.execute()
            testCoroutineScheduler.advanceTimeBy(1000)
            executed shouldBe  true
        }
        it("should only allow one subsystem action at a time (executing two in parallel, cancels the current one)") {
            var executed1 = false
            var executed2 = false
            val ss = testSubsystem(StandardTestDispatcher(testCoroutineScheduler))
            val ssAction1 = ss.TestSubSystemCommand {
                delay(100)
                executed1 = true
            }
            val ssAction2 = ss.TestSubSystemCommand {
                delay(10)
                executed2 = true
            }
            val p = Parallel(ssAction1, ssAction2)
            p.execute()
            executed1 shouldBe false
            executed2 shouldBe false
            testCoroutineScheduler.advanceTimeBy(1000)
            executed1 shouldBe  false
            executed2 shouldBe true
        }
        it("should only allow one subsystem action at a time (executing a new two in sequence, the second waits for the first to complete)") {
            var executed1 = false
            var executed2 = false
            var actions = mutableListOf<Int>()
            val ss = testSubsystem(StandardTestDispatcher(testCoroutineScheduler))
            val ssAction1 = ss.TestSubSystemCommand {
                delay(100)
                println("executed1")
                actions.add(1)
                executed1 = true
            }
            val ssAction2 = ss.TestSubSystemCommand {
                println("executed2")
                actions.add(2)
                executed2 = true
            }
            val s = Sequential(ssAction1, ssAction2, ssAction1)
            launch {  s.execute() }
            executed1 shouldBe false
            executed2 shouldBe false
            testCoroutineScheduler.advanceTimeBy(50)
            executed1 shouldBe false
            executed2 shouldBe false
            testCoroutineScheduler.advanceTimeBy(100)
            executed1 shouldBe  true
            executed2 shouldBe true
            actions.size shouldBe 2
            actions[0] shouldBe 1
            actions[1] shouldBe 2
            testCoroutineScheduler.advanceTimeBy(100)
            actions.size shouldBe 3
            actions[2] shouldBe 1

            executed1 shouldBe  true
            executed2 shouldBe true
        }
    }

})
