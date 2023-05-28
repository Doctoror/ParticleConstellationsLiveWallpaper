/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.userprefs.data

import android.content.res.Resources
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import com.doctoror.particleswallpaper.R

class DefaultSceneSettings(
    res: Resources,
    theme: Resources.Theme,
    typedValueFactory: TypedValueFactory = DefaultTypedValueFactory()
) {

    val backgroundColor = ResourcesCompat.getColor(res, R.color.defaultBackground, theme)

    val backgroundScroll = true

    val backgroundUri = NO_URI

    val density = res.getInteger(R.integer.defaultDensity)

    val frameDelay = res.getInteger(R.integer.defaultFrameDelay)

    val lineLength = res.getDimension(R.dimen.defaultLineLength)

    val lineScale = Math.max(1f, res.getDimension(R.dimen.defaultLineScale))

    val particleColor = ResourcesCompat.getColor(res, R.color.defaultParticleColor, theme)

    val particleScale = Math.max(0.5f, res.getDimension(R.dimen.defaultParticleScale))

    val particlesScroll = true

    val speedFactor = resolveSpeedFactor(res, typedValueFactory)

    private fun resolveSpeedFactor(
        res: Resources,
        typedValueFactory: TypedValueFactory
    ): Float {
        val outValue = typedValueFactory.newTypedValue()
        res.getValue(R.dimen.defaultSpeedFactor, outValue, true)
        return outValue.float
    }

    interface TypedValueFactory {
        fun newTypedValue(): TypedValue
    }

    private class DefaultTypedValueFactory : TypedValueFactory {
        override fun newTypedValue() = TypedValue()
    }
}
