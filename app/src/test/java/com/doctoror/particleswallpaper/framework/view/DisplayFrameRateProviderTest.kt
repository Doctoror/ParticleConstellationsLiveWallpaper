package com.doctoror.particleswallpaper.framework.view

import android.content.Context
import android.view.Display
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class DisplayFrameRateProviderTest {

    private val context: Context = mock()

    private val displayFrameRateProvider = DisplayFrameRateProvider()

    @Test
    fun returns60WhenNoDisplay() {
        whenever(context.display).thenReturn(null)

        val output = displayFrameRateProvider.provide(context)

        assertEquals(60, output)
    }

    @Test
    fun returnsRefreshRateFromDisplay() {
        val display: Display = mock()
        val refreshRate = 90f
        whenever(display.refreshRate).thenReturn(refreshRate)
        whenever(context.display).thenReturn(display)

        val output = displayFrameRateProvider.provide(context)

        assertEquals(refreshRate.toInt(), output)
    }
}
