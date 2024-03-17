package edu.ncssm.ftc.electricmayhem.tests.core

import android.util.Log
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener
import io.mockk.every
import io.mockk.mockkStatic

class MyKotestProjectListener : ProjectListener {
    override suspend fun beforeProject() {
        mockkStatic(Log::class)
        every { Log.isLoggable(any(),any()) } answers { true }
        every { Log.v(any(), any()) } answers { println("VERBOSE: ${args[0]} - ${args[1]}"); 0 }
        every { Log.d(any(), any()) } answers { println("DEBUG: ${args[0]} - ${args[1]}"); 0 }
        every { Log.i(any(), any()) } answers { println("INFO: ${args[0]} - ${args[1]}"); 0 }
        every { Log.e(any(), any()) } answers { println("ERROR: ${args[0]} - ${args[1]}"); 0 }
    }
}
@Suppress("unused")
object ProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(MyKotestProjectListener())
}