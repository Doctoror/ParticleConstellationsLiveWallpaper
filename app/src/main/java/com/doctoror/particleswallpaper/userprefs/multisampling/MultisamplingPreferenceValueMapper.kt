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

class MultisamplingPreferenceValueMapper(private val resources: Resources) {

    private fun ensure0Exists(supportedValues: Set<String>) = if (!supportedValues.contains("0")) {
        supportedValues.plus("0")
    } else {
        supportedValues
    }

    fun toEntries(supportedValues: Set<String>) = ensure0Exists(supportedValues)
        .asSequence()
        .sorted()
        .map {
            resources.getText(
                when (it) {
                    "0" -> R.string.Disabled_best_performance
                    "2" -> R.string.two_x_msaa
                    "4" -> R.string.four_x_msaa_best_appearance
                    else -> throw IllegalArgumentException("Illegal multisampling value: $it")
                }
            )
        }
        .toList()
        .toTypedArray()

    fun toEntryValues(supportedValues: Set<String>) = ensure0Exists(supportedValues)
        .asSequence()
        .sorted()
        .toList()
        .toTypedArray<CharSequence>()
}
