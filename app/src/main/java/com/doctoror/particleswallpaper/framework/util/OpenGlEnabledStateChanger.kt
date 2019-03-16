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

class OpenGlEnabledStateChanger(
    private val context: Context,
    private val deviceSettings: DeviceSettings
) {

    fun setOpenglGlEnabled(openGlEnabled: Boolean, shouldKillApp: Boolean) {
        if (deviceSettings.openglEnabled != openGlEnabled) {
            deviceSettings.openglEnabled = openGlEnabled

            val canvasServiceComponentName = ComponentName(
                context,
                CanvasWallpaperServiceImpl::class.java
            )

            val glServiceComponentName = ComponentName(
                context,
                GlWallpaperServiceImpl::class.java
            )

            val componentToEnable: ComponentName
            val componentToDisable: ComponentName

            if (openGlEnabled) {
                componentToEnable = glServiceComponentName
                componentToDisable = canvasServiceComponentName
            } else {
                componentToEnable = canvasServiceComponentName
                componentToDisable = glServiceComponentName
            }

            context
                .packageManager
                .setComponentEnabledSetting(
                    componentToEnable,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )

            context
                .packageManager
                .setComponentEnabledSetting(
                    componentToDisable,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    if (shouldKillApp) 0 else PackageManager.DONT_KILL_APP
                )
        }
    }
}
