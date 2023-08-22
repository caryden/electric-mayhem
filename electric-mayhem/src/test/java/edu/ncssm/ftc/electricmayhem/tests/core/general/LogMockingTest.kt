package edu.ncssm.ftc.electricmayhem.tests.core.general


import android.util.Log
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockkStatic

class LogMockingTest : FunSpec()  {
    init {
        test("test output/logging mocks") {
            println("Hello World")
            Log.v("tag", "Hello Log.v")
            Log.i("tag", "Hello Log.")
            Log.d("tag", "Hello Log.d")
            Log.e("tag", "Hello Log.e")
        }
    }
}