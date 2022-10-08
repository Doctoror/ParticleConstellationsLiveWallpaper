/*
 * Copyright (C) 2018 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particleswallpaper.framework.opengl

import com.doctoror.particlesdrawable.opengl.chooser.NoMatchingConfigsException
import com.doctoror.particleswallpaper.framework.util.OpenGlEnabledStateChanger
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import io.reactivex.exceptions.OnErrorNotImplementedException
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class KnownOpenglIssuesHandlerTest {

    private val deviceSettings: DeviceSettings = mock()
    private val openglEnabledStateChanger: OpenGlEnabledStateChanger = mock()
    private val underTest = KnownOpenglIssuesHandler(deviceSettings, openglEnabledStateChanger)

    @Test
    fun doesNotDisableOpenGlAndReturnsFalseOnNonMatchingConfigsException() {
        val result = underTest.handleUncaughtException(Exception())
        verify(deviceSettings, never()).openglSupported = any()
        verify(openglEnabledStateChanger, never()).setOpenglGlEnabled(any(), any())
        assertFalse(result)
    }

    @Test
    fun disablesOpenGlAndReturnsTrueOnNoMatchingConfigsException() {
        val result = underTest.handleUncaughtException(NoMatchingConfigsException())
        verify(deviceSettings).openglSupported = false
        verify(openglEnabledStateChanger)
            .setOpenglGlEnabled(openGlEnabled = false, shouldKillApp = true)
        assertTrue(result)
    }

    @Test
    fun doesNotDisableOpenGlAndReturnsFalseOnOnErrorNotImplementedException() {
        val result = underTest.handleUncaughtException(OnErrorNotImplementedException(Exception()))
        verify(deviceSettings, never()).openglSupported = any()
        verify(openglEnabledStateChanger, never()).setOpenglGlEnabled(any(), any())
        assertFalse(result)
    }

    @Test
    fun disablesOpenGlAndReturnsTrueOnNoMatchingConfigsExceptionAsCause() {
        val result = underTest.handleUncaughtException(
            OnErrorNotImplementedException(NoMatchingConfigsException())
        )
        verify(deviceSettings).openglSupported = false
        verify(openglEnabledStateChanger)
            .setOpenglGlEnabled(openGlEnabled = false, shouldKillApp = true)
        assertTrue(result)
    }
}
