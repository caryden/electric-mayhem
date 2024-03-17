package edu.ncssm.ftc.electricmayhem.tests.core.sensors

import edu.ncssm.ftc.electricmayhem.core.sensors.and
import edu.ncssm.ftc.electricmayhem.core.sensors.data.SensorData
import edu.ncssm.ftc.electricmayhem.core.sensors.goesActive
import edu.ncssm.ftc.electricmayhem.core.sensors.not
import edu.ncssm.ftc.electricmayhem.core.sensors.or
import edu.ncssm.ftc.electricmayhem.core.sensors.window
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

@OptIn(ExperimentalKotest::class)
class FlowExtensionsTests : DescribeSpec({
    describe("Given a Flow<T> from a Sensor")
        context("When Intermediate flow extensions are used") {
            it("window should output a list of the specified size and data") {
                val windowSize = 2
                val elements = listOf(false, false, true, true, false)
                val outputList = MutableList<List<Boolean>>(0) { listOf() }
                elements.asFlow().window(2).toList(outputList)
                outputList.size shouldBe elements.size - (windowSize - 1)
                outputList.onEach { list -> list.size shouldBe windowSize }
                outputList.onEachIndexed() { i, list ->
                    list[0] shouldBe elements[i]
                    list [1] shouldBe elements[i + 1]
                }
            }
            it("goesActive should emit on a false to true transition only") {
                val elements = listOf(false, false, true, true, false)
                val outputList = MutableList<Boolean>(0) { false }
                elements.asFlow()
                    .map { SensorData(it) }
                    .goesActive()
                    .toList(outputList)
                outputList.size shouldBe 1
            }
            it("goesInactive should emit on a true to false transition only") {
                val elements = listOf(false, false, true, true, false)
                val outputList = MutableList<Boolean>(0) { false }
                elements.asFlow()
                    .map { SensorData(it) }
                    .goesActive()
                    .toList(outputList)
                outputList.size shouldBe 1
            }
            it("and() should produce a flow that is the logical and of any two boolean flows"){
                val elements = listOf(false, true, true, true, false)
                val elements2 = listOf(true, false, true, true, false)
                val expectedAnd = listOf(false,false,true,true,false)
                val outputList = MutableList<Boolean>(0) { false }
                elements.asFlow().and(elements2.asFlow())
                    .toList(outputList)
                outputList.size shouldBe expectedAnd.size
                outputList.forEachIndexed() { i, v ->
                    v shouldBe expectedAnd[i]
                }
            }
            it("or() should produce a flow that is the logical or of any two boolean flows"){
                val elements = listOf(false, true, true, true, false)
                val elements2 = listOf(true, false, true, true, false)
                val expectedAnd = listOf(true,true,true,true,false)
                val outputList = MutableList<Boolean>(0) { false }
                elements.asFlow().or(elements2.asFlow())
                    .toList(outputList)
                outputList.size shouldBe expectedAnd.size
                outputList.forEachIndexed() { i, v ->
                    v shouldBe expectedAnd[i]
                }
            }
            it("not() should produce a flow that is the logical not of a boolean flow"){
                val elements = listOf(false, true, true, true, false)
                val expectedNot = listOf(true,false,false,false,true)
                val outputList = MutableList<Boolean>(0) { false }
                elements.asFlow().not()
                    .toList(outputList)
                outputList.size shouldBe expectedNot.size
                outputList.forEachIndexed() { i, v ->
                    v shouldBe expectedNot[i]
                }
            }

        }
})
