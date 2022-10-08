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
package com.doctoror.particleswallpaper.userprefs.multisampling

import android.content.res.Resources
import com.doctoror.particleswallpaper.R
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class MultisamplingPreferenceValueMapperTest {

    private val resources: Resources = mock {
        on { it.getText(any()) }.thenAnswer { invocation ->
            (invocation.getArgument(0) as Int).toString()
        }
    }

    private val underTest = MultisamplingPreferenceValueMapper(resources)

    @Test
    fun mapsToEmptyEntries() {
        val result = underTest.toEntries(emptySet())
        assertTrue(Arrays.equals(arrayOf(R.string.Disabled_best_performance.toString()), result))
    }

    @Test
    fun mapsToEmptyEntryValues() {
        val result = underTest.toEntryValues(emptySet())
        assertTrue(Arrays.equals(arrayOf("0"), result))
    }

    @Test
    fun mapsToEntriesWhen4And2Supported() {
        val expectedEnties = arrayOf<CharSequence>(
            R.string.Disabled_best_performance.toString(),
            R.string.two_x_msaa.toString(),
            R.string.four_x_msaa_best_appearance.toString()
        )

        val result = underTest.toEntries(setOf("4", "2"))

        assertTrue(Arrays.equals(expectedEnties, result))
    }

    @Test
    fun mapsToEntriesWhen2Supported() {
        val expectedEntries = arrayOf<CharSequence>(
            R.string.Disabled_best_performance.toString(),
            R.string.two_x_msaa.toString()
        )

        val result = underTest.toEntries(setOf("2"))

        assertTrue(Arrays.equals(expectedEntries, result))
    }

    @Test
    fun mapsToEntryValues() {
        val expectedEntryValues = arrayOf<CharSequence>("0", "2", "4")

        val result = underTest.toEntryValues(setOf("4", "2"))

        assertTrue(Arrays.equals(expectedEntryValues, result))
    }
}
