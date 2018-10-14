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

class MultisamplingSupportDetector(
    private val deviceSettings: DeviceSettings
) {

    /**
     * Compares requested and chosen multisampling modes and writes to
     * [DeviceSettings.multisamplingSupported] when detected as unsupported at all.
     */
    fun writeMultisamplingSupportStatus(
        requestedNumSamples: Int,
        actualNumSamples: Int
    ) {
        // When multisampling requested as 4, it means both 4 and 2 are not supported.
        // Only then we want to mark this as unsupported.
        if (requestedNumSamples == 4 && actualNumSamples == 0)
            deviceSettings.multisamplingSupported = false
    }
}
