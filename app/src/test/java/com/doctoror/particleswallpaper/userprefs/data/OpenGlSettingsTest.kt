package com.doctoror.particleswallpaper.userprefs.data

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
