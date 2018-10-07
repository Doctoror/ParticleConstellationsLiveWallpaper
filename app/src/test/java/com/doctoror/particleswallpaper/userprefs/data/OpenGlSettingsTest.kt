package com.doctoror.particleswallpaper.userprefs.data

import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OpenGlSettingsTest {

    private val prefs: SharedPreferences = mock()

    private val underTest = OpenGlSettings(prefs)

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
        val underTest = OpenGlSettings(prefs)

        val o = underTest.observeNumSamples().test()

        underTest.numSamples = 1
        underTest.numSamples = 2

        o.assertValues(4, 1, 2)
    }

    @Test
    fun returnsOptimizeTexturesValueFromPrefs() {
        whenever(prefs.getBoolean(KEY_OPTIMIZE_TEXTURES, true))
            .thenReturn(true)

        assertEquals(true, underTest.optimizeTextures)
    }

    @Test
    fun storessOptimizeTexturesValueInPrefs() {
        val editor: SharedPreferences.Editor = mock()
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)
        whenever(prefs.edit()).thenReturn(editor)

        underTest.optimizeTextures = true

        verify(editor).putBoolean(KEY_OPTIMIZE_TEXTURES, true)
        verify(editor).apply()
    }

    @Test
    fun observesOptimizeTexturesChanges() {
        val prefs = InMemorySharedPreferences()
        val underTest = OpenGlSettings(prefs)

        val o = underTest.observeOptimizeTextures().test()

        underTest.optimizeTextures = false
        underTest.optimizeTextures = true

        o.assertValues(true, false, true)
    }
}
