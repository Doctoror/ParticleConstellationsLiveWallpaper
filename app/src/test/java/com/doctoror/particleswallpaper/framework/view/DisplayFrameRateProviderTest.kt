package com.doctoror.particleswallpaper.framework.view

import android.app.Application
import android.content.Context
import android.os.Build
import android.view.Display
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class DisplayFrameRateProviderTest {

    private val context: Context = mock()

    private val displayFrameRateProvider = DisplayFrameRateProvider()

    @Test
    @Config(sdk = intArrayOf(Build.VERSION_CODES.R))
    fun returns60OnNoSuchMethodError() {
        whenever(context.display).thenThrow(NoSuchMethodError())

        val output = displayFrameRateProvider.provide(context)

        assertEquals(60, output)
    }

    @Test
    @Config(sdk = intArrayOf(Build.VERSION_CODES.R))
    fun returnsRefreshRateFromDisplay() {
        val display: Display = mock()
        val refreshRate = 90f
        whenever(display.refreshRate).thenReturn(refreshRate)
        whenever(context.display).thenReturn(display)

        val output = displayFrameRateProvider.provide(context)

        assertEquals(refreshRate.toInt(), output)
    }
}
