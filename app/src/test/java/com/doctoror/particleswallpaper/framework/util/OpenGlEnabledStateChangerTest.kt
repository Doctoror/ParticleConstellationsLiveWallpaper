/*
 * Copyright (C) 2019 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.framework.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.doctoror.particleswallpaper.engine.canvas.CanvasWallpaperServiceImpl
import com.doctoror.particleswallpaper.engine.opengl.GlWallpaperServiceImpl
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenGlEnabledStateChangerTest {

    private val packageManager: PackageManager = mock()

    private val context: Context = mock {
        on(it.packageManager).thenReturn(packageManager)
    }

    private val deviceSettings: DeviceSettings = mock()

    private val underTest = OpenGlEnabledStateChanger(context, deviceSettings)

    @Test
    fun doesNotDisableOpenGlWhenAlreadyDisabled() {
        underTest.setOpenglGlEnabled(
            openGlEnabled = false,
            shouldKillApp = false
        )

        verifyNoInteractions(packageManager)
    }

    @Test
    fun disablesOpenGLAndDoesNotKillApp() {
        whenever(deviceSettings.openglEnabled).thenReturn(true)

        underTest.setOpenglGlEnabled(
            openGlEnabled = false,
            shouldKillApp = false
        )

        testEnablesAndDisablesComponents(
            componentToEnableClassName = CanvasWallpaperServiceImpl::class.java.name,
            componentToDisableClassName = GlWallpaperServiceImpl::class.java.name,
            shouldKillApp = false
        )
    }

    @Test
    fun disablesOpenGLAndKillsApp() {
        whenever(deviceSettings.openglEnabled).thenReturn(true)

        underTest.setOpenglGlEnabled(
            openGlEnabled = false,
            shouldKillApp = true
        )

        testEnablesAndDisablesComponents(
            componentToEnableClassName = CanvasWallpaperServiceImpl::class.java.name,
            componentToDisableClassName = GlWallpaperServiceImpl::class.java.name,
            shouldKillApp = true
        )
    }

    @Test
    fun doesNotEnableOpenGlWhenAlreadyEnabled() {
        whenever(deviceSettings.openglEnabled).thenReturn(true)

        underTest.setOpenglGlEnabled(
            openGlEnabled = true,
            shouldKillApp = false
        )

        verifyNoInteractions(packageManager)
    }

    @Test
    fun enablesOpenGLAndDoesNotKillApp() {
        underTest.setOpenglGlEnabled(
            openGlEnabled = true,
            shouldKillApp = false
        )

        testEnablesAndDisablesComponents(
            componentToEnableClassName = GlWallpaperServiceImpl::class.java.name,
            componentToDisableClassName = CanvasWallpaperServiceImpl::class.java.name,
            shouldKillApp = false
        )
    }

    @Test
    fun enablesOpenGLAndKillsApp() {
        underTest.setOpenglGlEnabled(
            openGlEnabled = true,
            shouldKillApp = true
        )

        testEnablesAndDisablesComponents(
            componentToEnableClassName = GlWallpaperServiceImpl::class.java.name,
            componentToDisableClassName = CanvasWallpaperServiceImpl::class.java.name,
            shouldKillApp = true
        )
    }

    private fun testEnablesAndDisablesComponents(
        componentToEnableClassName: String,
        componentToDisableClassName: String,
        shouldKillApp: Boolean
    ) {
        val inorder = inOrder(packageManager)

        val componentToEnableCaptor = argumentCaptor<ComponentName>()
        inorder.verify(packageManager).setComponentEnabledSetting(
            componentToEnableCaptor.capture(),
            eq(PackageManager.COMPONENT_ENABLED_STATE_ENABLED),
            eq(PackageManager.DONT_KILL_APP)
        )

        val componentToDisableCaptor = argumentCaptor<ComponentName>()
        inorder.verify(packageManager).setComponentEnabledSetting(
            componentToDisableCaptor.capture(),
            eq(PackageManager.COMPONENT_ENABLED_STATE_DISABLED),
            eq(if (shouldKillApp) 0 else PackageManager.DONT_KILL_APP)
        )

        assertEquals(
            componentToEnableClassName,
            componentToEnableCaptor.firstValue.className
        )

        assertEquals(
            componentToDisableClassName,
            componentToDisableCaptor.firstValue.className
        )
    }
}
