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
package com.doctoror.particleswallpaper.framework.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.withStyledAttributes
import com.doctoror.particleswallpaper.R
import com.doctoror.particleswallpaper.app.Preference

open class SeekBarPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Preference(context, attrs, defStyle), OnSeekBarChangeListener {

    private var trackingTouch = false

    var max: Int = 0
        set(value) {
            if (field != value) {
                field = value
                notifyChanged()
            }
        }

    var progress: Int = 0
        set(value) {
            var newValue = value
            if (value > max) {
                newValue = max
            }
            if (value < 0) {
                newValue = 0
            }
            if (field != newValue) {
                field = newValue
                persist(newValue)
                notifyChanged()
            }
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.SeekBarPreference, defStyle, 0) {
            max = getInt(R.styleable.SeekBarPreference_max, max)
        }
        layoutResId = R.layout.preference_widget_seekbar
    }

    override fun onBindView(view: View) {
        super.onBindView(view)
        val seekBar = view.findViewById<SeekBar>(R.id.seekbar)
        seekBar.setOnSeekBarChangeListener(this)
        seekBar.max = max
        seekBar.progress = progress
        seekBar.isEnabled = isEnabled
    }

    /**
     * Persist the seekBar's progress value if callChangeListener returns true,
     * otherwise set the seekBar's progress to the stored value
     */
    private fun syncProgress(seekBar: SeekBar) {
        val syncValue = seekBar.progress
        if (progress != syncValue) {
            if (callChangeListener(syncValue)) {
                progress = syncValue
            } else {
                seekBar.progress = progress
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser && !trackingTouch) {
            syncProgress(seekBar)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        trackingTouch = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        trackingTouch = false
        if (seekBar.progress != progress) {
            syncProgress(seekBar)
        }
    }
}
