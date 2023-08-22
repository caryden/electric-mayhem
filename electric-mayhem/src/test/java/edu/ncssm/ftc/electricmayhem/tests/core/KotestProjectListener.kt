package edu.ncssm.ftc.electricmayhem.tests.core

import android.util.Log
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener
import io.mockk.every
import io.mockk.mockkStatic

class MyKotestProjectListener : ProjectListener {
    override suspend fun beforeProject() {
        mockkStatic(Log::class)
        every { Log.v(any<String>(), any<String>()) } answers { println("VERBOSE: ${args[0]} - ${args[1]}"); 0 }
        every { Log.d(any<String>(), any<String>()) } answers { println("DEBUG: ${args[0]} - ${args[1]}"); 0 }
        every { Log.i(any<String>(), any<String>()) } answers { println("INFO: ${args[0]} - ${args[1]}"); 0 }
        every { Log.e(any<String>(), any<String>()) } answers { println("ERROR: ${args[0]} - ${args[1]}"); 0 }
    }
}

object ProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(MyKotestProjectListener())
}
