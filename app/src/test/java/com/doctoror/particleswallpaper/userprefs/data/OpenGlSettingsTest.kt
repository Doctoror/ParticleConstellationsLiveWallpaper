package com.doctoror.particleswallpaper.userprefs.data

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class OpenGlSettingsTest {

    private val prefs: SharedPreferences = mock()

    private val underTest = OpenGlSettings { prefs }

    @Test
    fun returnsNumSamplesValueFromPrefs() {
        whenever(prefs.getInt(KEY_NUM_SAMPLES, 4))
            .thenReturn(4)

        assertEquals(4, underTest.numSamples)
    }

    @Test
    fun storessNumSamplesValueInPrefs() {
        val editor: SharedPreferences.Editor = mock()
        whenever(editor.putInt(any(), any())).thenReturn(editor)
        whenever(prefs.edit()).thenReturn(editor)

        underTest.numSamples = 1

        verify(editor).putInt(KEY_NUM_SAMPLES, 1)
        verify(editor).apply()
    }

    @Test
    fun observesNumSamplesChanges() {
        val prefs = InMemorySharedPreferences()
        val underTest = OpenGlSettings { prefs }

        val o = underTest.observeNumSamples().test()

        underTest.numSamples = 1
        underTest.numSamples = 2

        o.assertValues(4, 1, 2)
    }
}
