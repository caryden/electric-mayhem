package edu.ncssm.ftc.electricmayhem.tests.core

import android.util.Log
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener
import io.mockk.every
import io.mockk.mockkStatic

class MyKotestProjectListener : ProjectListener {
    override suspend fun beforeProject() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
//        every { Log.isLoggable(any(), any()) } returns false
    }
}

object ProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(MyKotestProjectListener())
}
