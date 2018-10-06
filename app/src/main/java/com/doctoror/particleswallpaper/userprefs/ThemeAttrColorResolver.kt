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
package com.doctoror.particleswallpaper.userprefs

import android.content.res.Resources.Theme
import android.graphics.Color
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt

class ThemeAttrColorResolver {

    /**
     * Returns color for attr from the [Theme]
     *
     * @param theme [Theme] to get int from
     * @param attr  Attribute of the int
     * @return dimension for attr from the [Theme]
     */
    @ColorInt
    fun getColor(theme: Theme, @AttrRes attr: Int): Int {
        val array = theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            return array.getColor(0, Color.TRANSPARENT)
        } finally {
            array.recycle()
        }
    }
}
