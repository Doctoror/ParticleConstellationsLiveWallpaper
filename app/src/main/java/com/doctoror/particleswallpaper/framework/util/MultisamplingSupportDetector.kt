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

import com.doctoror.particleswallpaper.userprefs.data.DeviceSettings
import com.doctoror.particleswallpaper.userprefs.data.OpenGlSettings

class MultisamplingSupportDetector(
    private val deviceSettings: DeviceSettings,
    private val openGlSettings: OpenGlSettings
) {
    /**
     * Compares requested and chosen multisampling modes and writes unsupported values to
     * [DeviceSettings.multisamplingSupportedValues].
     */
    fun writeMultisamplingSupportStatus(
        requestedNumSamples: Int,
        actualNumSamples: Int
    ) {
        if (actualNumSamples != requestedNumSamples) {

            val supportedMultisamplingValues = deviceSettings
                .multisamplingSupportedValues
                .toMutableSet()
                .apply { removeAll { it.toInt() > actualNumSamples } }

            deviceSettings.multisamplingSupportedValues = supportedMultisamplingValues
            openGlSettings.numSamples = actualNumSamples
        }
    }
}
