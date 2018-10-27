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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.microedition.khronos.egl.EGL10

class MultisamplingConfigSpecParserTest {

    private val underTest = MultisamplingConfigSpecParser()

    @Test
    fun returns0ForNullInput() {
        val result = underTest.extractNumSamplesFromConfigSpec(null)
        assertEquals(0, result)
    }

    @Test
    fun returns0ForEmptyInput() {
        val result = underTest.extractNumSamplesFromConfigSpec(intArrayOf())
        assertEquals(0, result)
    }

    @Test
    fun returns0WhenValueNotFound() {
        val result = underTest.extractNumSamplesFromConfigSpec(
            intArrayOf(
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_NONE
            )
        )
        assertEquals(0, result)
    }

    @Test
    fun returnsValueFromInput() {
        val samples = 4
        val result = underTest.extractNumSamplesFromConfigSpec(
            intArrayOf(
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_SAMPLE_BUFFERS, 1,
                EGL10.EGL_SAMPLES, samples,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_NONE
            )
        )
        assertEquals(samples, result)
    }
}
