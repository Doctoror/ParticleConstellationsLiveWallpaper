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

    private val multisamplingAllowedValues = setOf(0, 2, 4)

    /**
     * Compares requested and chosen multisampling modes and writes unsupported values to
     * [DeviceSettings.multisamplingSupportedValues].
     *
     * The [com.doctoror.particlesdrawable.opengl.util.MultisampleConfigChooser] supports values 4
     * and 2. When 4 is passed but failed it tries 2 then if failed fallbacks to 0.
     */
    fun writeMultisamplingSupportStatus(
        requestedNumSamples: Int,
        actualNumSamples: Int
    ) {
        if (actualNumSamples != requestedNumSamples) {

            if (actualNumSamples !in multisamplingAllowedValues) {
                throw IllegalArgumentException("Invalid value passed as actualNumSamples")
            }

            if (requestedNumSamples !in multisamplingAllowedValues) {
                throw IllegalArgumentException("Invalid value passed as actualNumSamples")
            }

            when (requestedNumSamples) {
                4 -> deviceSettings.multisamplingSupportedValues = if (actualNumSamples == 2) {
                    /*
                     * When 4 requested and actual is 2 then 2 is the only supported value.
                     */
                    setOf("2")
                } else {

                    /*
                     * When 4 requested and actual is 0 then there are no supported values.
                     */
                    emptySet()
                }

                /*
                 * When 2 requested and actual is 0 then there are no supported values.
                 */
                2 -> deviceSettings.multisamplingSupportedValues = emptySet()
            }

            openGlSettings.numSamples = actualNumSamples
        }
    }
}
