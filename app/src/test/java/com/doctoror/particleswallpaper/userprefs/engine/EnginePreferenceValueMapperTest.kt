package com.doctoror.particleswallpaper.userprefs.engine

import android.content.res.Resources
import com.doctoror.particleswallpaper.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import java.util.Arrays


private const val ENTRY_OPENGL = "OpenGL"
private const val ENTRY_CANVAS = "Canvas"

private const val VALUE_OPENGL = "opengl"
private const val VALUE_CANVAS = "canvas"

class EnginePreferenceValueMapperTest {

    private val resources: Resources = mock {
        on(it.getText(R.string.OpenGL)).thenReturn(ENTRY_OPENGL)
        on(it.getText(R.string.Canvas)).thenReturn(ENTRY_CANVAS)
    }

    private val underTest = EnginePreferenceValueMapper(resources)

    @Test
    fun providesEntries() {
        val entries = underTest.provideEntries()
        assertTrue(Arrays.equals(entries, arrayOf<CharSequence>(ENTRY_OPENGL, ENTRY_CANVAS)))
    }

    @Test
    fun providesEntryValues() {
        val entries = underTest.provideEntryValues()
        assertTrue(
            Arrays.equals(
                entries, arrayOf<CharSequence>(
                    VALUE_OPENGL,
                    VALUE_CANVAS
                )
            )
        )
    }

    @Test
    fun valueToEnabledStateIsFalseForNull() {
        assertFalse(underTest.valueToOpenglEnabledState(null))
    }

    @Test
    fun valueToEnabledStateIsTrueForOpenGL() {
        assertTrue(underTest.valueToOpenglEnabledState(VALUE_OPENGL))
    }

    @Test
    fun valueToEnabledStateIsFalseForCanvas() {
        assertFalse(underTest.valueToOpenglEnabledState(VALUE_CANVAS))
    }

    @Test
    fun valueToEnabledStateThrowsForUnexpectedValue() {
        assertThrows(IllegalArgumentException::class.java) {
            underTest.valueToOpenglEnabledState("Canvas")
        }
    }

    @Test
    fun valueForOpenEnabledStateIsOpenGl() {
        assertEquals(VALUE_OPENGL, underTest.openglEnabledStateToValue(true))
    }

    @Test
    fun valueForOpenDisabledStateIsCanvas() {
        assertEquals(VALUE_CANVAS, underTest.openglEnabledStateToValue(false))
    }
}
