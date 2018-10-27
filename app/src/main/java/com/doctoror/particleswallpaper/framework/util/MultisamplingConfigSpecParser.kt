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

import javax.microedition.khronos.egl.EGL10

class MultisamplingConfigSpecParser {

    fun extractNumSamplesFromConfigSpec(config: IntArray?) = if (config == null) {
        0
    } else {
        var returnValue = 0
        val sampleBuffersIndex = config.indexOf(EGL10.EGL_SAMPLE_BUFFERS)
        if (sampleBuffersIndex != -1) {
            val sampleBuffers = config[sampleBuffersIndex + 1] == 1
            if (sampleBuffers) {
                val samplesIndex = config.indexOf(EGL10.EGL_SAMPLES)
                if (samplesIndex != -1) {
                    returnValue = config[samplesIndex + 1]
                }
            }
        }
        returnValue
    }
}
