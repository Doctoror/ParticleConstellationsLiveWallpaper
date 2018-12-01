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
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.Test

class MultisamplingSupportDetectorTest {

    private val deviceSettings: DeviceSettings = mock {
        on { it.multisamplingSupportedValues }.thenReturn(setOf("2", "4"))
    }

    private val openGlSettings: OpenGlSettings = mock()

    private val underTest = MultisamplingSupportDetector(deviceSettings, openGlSettings)

    @Test
    fun writesNoValuesWhenRequestedEqualsActual() {
        underTest.writeMultisamplingSupportStatus(4, 4)
        verifyZeroInteractions(deviceSettings)
        verifyZeroInteractions(openGlSettings)
    }

    @Test
    fun writesValue2WhenRequested4Actual2() {
        underTest.writeMultisamplingSupportStatus(4, 2)
        verify(deviceSettings).multisamplingSupportedValues = setOf("2")
    }

    @Test
    fun writesEmptyValueWhenRequested4Actual0() {
        underTest.writeMultisamplingSupportStatus(4, 0)
        verify(deviceSettings).multisamplingSupportedValues = emptySet()
    }

    @Test
    fun writesEmptyValueWhenRequested2Actual0() {
        underTest.writeMultisamplingSupportStatus(2, 0)
        verify(deviceSettings).multisamplingSupportedValues = emptySet()
    }

    @Test
    fun writesActualNumSampplesWhenDoesNotMatchRequested() {
        underTest.writeMultisamplingSupportStatus(4, 0)
        verify(openGlSettings).numSamples = 0
    }
}
