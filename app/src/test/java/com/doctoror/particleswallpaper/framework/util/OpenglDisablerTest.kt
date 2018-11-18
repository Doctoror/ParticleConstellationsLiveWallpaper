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
package com.doctoror.particleswallpaper.framework.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.doctoror.particleswallpaper.engine.opengl.GlWallpaperServiceImpl
import com.doctoror.particleswallpaper.engine.canvas.CanvasWallpaperServiceImpl
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenglDisablerTest {

    private val packageManager: PackageManager = mock()

    private val context: Context = mock {
        on(it.packageName).thenReturn("packageName")
        on(it.packageManager).thenReturn(packageManager)
    }

    private val deviceSettings: DeviceSettings = mock()

    private val underTest = OpenglDisabler(context, deviceSettings)

    @Test
    fun disablesOpenGl() {
        underTest.disableOpenGl()

        val canvasServiceComponentName = ComponentName(
            context,
            CanvasWallpaperServiceImpl::class.java
        )

        val glServiceComponentName = ComponentName(
            context,
            GlWallpaperServiceImpl::class.java
        )

        verify(deviceSettings).openglEnabled = false
        verify(packageManager).setComponentEnabledSetting(
            canvasServiceComponentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        verify(packageManager).setComponentEnabledSetting(
            glServiceComponentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            0
        )
    }
}
