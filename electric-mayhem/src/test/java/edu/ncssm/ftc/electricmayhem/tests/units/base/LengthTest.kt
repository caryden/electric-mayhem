package edu.ncssm.ftc.electricmayhem.tests.units.base

import edu.ncssm.ftc.electricmayhem.units.base.*
import edu.ncssm.ftc.electricmayhem.units.*
import edu.ncssm.ftc.electricmayhem.units.derived.Force
import edu.ncssm.ftc.electricmayhem.units.derived.Power
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LengthTest : FunSpec({

    test("general") {
        val l = Length(1.0)
        val l1 = 10.0 * Length.Inches
        val l2 = 10.0 * Length.Meters
        val l3 = l1 + l2
        val l4 = l2 - l1

        val m = 1.0 * Mass.Kilograms
        val f = 1.0 * Force.Newtons
        val f2 = 1.0 * Mass.Kilograms * Length.Meters / ( Time.Seconds  * Time.Seconds)
        val test = f == f2
        test shouldBe true

        val f4 = f + f2
        f4.value shouldBe 2.0

        val f3 = 1.0 * Mass.Kilograms * Length.Meters per Time.Seconds.squared()
        val test2 = f == f3
        test2 shouldBe true

        val a0 = 1.0 * Angle.Degrees
        val w0 = 1.0 * Power.Watts
    }
})

