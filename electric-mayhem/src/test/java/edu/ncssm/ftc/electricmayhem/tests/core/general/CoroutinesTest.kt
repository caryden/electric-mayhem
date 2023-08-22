package edu.ncssm.ftc.electricmayhem.tests.core.general

import android.util.Log
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class CoroutinesTest : FunSpec() {
    private var lock = Any()

    init {
       // coroutineTestScope = true

        test("Coroutines should handle blocking IO") {
            val blockMillis = 10L
            var oneRan = false
            var twoRan = false
            // write a test that launch mutliple coroutines that block on IO
            // and make sure they don't block each other.
            CoroutineScope(Dispatchers.IO).launch {
                blockingMethod(blockMillis)
                oneRan = true
            }
            CoroutineScope(Dispatchers.IO).launch {
                blockingMethod(blockMillis)
                twoRan = true
            }
            oneRan shouldBe false
            twoRan shouldBe false

            Thread.sleep(blockMillis + blockMillis / 2)
            oneRan shouldBe true
            twoRan shouldBe false

            Thread.sleep(blockMillis)
            oneRan shouldBe true
            twoRan shouldBe true
        }
    }

    private fun blockingMethod(millis : Long) {
        // this fucntion is meant to simulate the synchronized write to a device in the FTC SDk
        synchronized(lock) {
            try {
                // wait for 100ms to simulate a write to a device
                Thread.sleep(millis)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

}