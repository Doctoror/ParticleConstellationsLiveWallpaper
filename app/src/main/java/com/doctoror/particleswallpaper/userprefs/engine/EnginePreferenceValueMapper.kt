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
package com.doctoror.particleswallpaper.userprefs.engine

import android.content.res.Resources
import com.doctoror.particleswallpaper.R

private const val VALUE_OPENGL = "opengl"
private const val VALUE_CANVAS = "canvas"

class EnginePreferenceValueMapper(private val resources: Resources) {

    fun provideEntries() = arrayOf(
        resources.getText(R.string.OpenGL),
        resources.getText(R.string.Canvas)
    )

    fun provideEntryValues() = arrayOf(
        VALUE_OPENGL,
        VALUE_CANVAS
    )

    fun valueToOpenglEnabledState(value: CharSequence?) = when (value) {
        VALUE_CANVAS, null -> false
        VALUE_OPENGL -> true
        else -> throw IllegalArgumentException("Unexpected value for engine: $value")
    }

    fun openglEnabledStateToValue(enabled: Boolean) = when (enabled) {
        true -> VALUE_OPENGL
        else -> VALUE_CANVAS
    }
}
