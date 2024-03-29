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
package com.doctoror.particleswallpaper.userprefs

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import com.doctoror.particleswallpaper.framework.view.setBackgroundBitmap
import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings

class SceneBackgroundView(
    private val deviceSettings: DeviceSettings,
    private val windowProvider: () -> Window
) {

    var particlesView: View? = null

    fun displayBackgroundColor(color: Int) {
        if (deviceSettings.openglEnabled) {
            particlesView?.setBackgroundColor(color)
        } else {
            windowProvider().setBackgroundDrawable(ColorDrawable(color))
        }
    }

    fun displayBackground(background: Bitmap?) {
        particlesView?.setBackgroundBitmap(background)
    }
}
