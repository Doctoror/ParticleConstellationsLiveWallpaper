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
import android.content.res.TypedArray
import android.os.Parcel
import android.os.Parcelable
import android.preference.Preference
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.doctoror.particleswallpaper.R

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
                persistInt(newValue)
                notifyChanged()
            }
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference, defStyle, 0)
        max = a.getInt(R.styleable.SeekBarPreference_max, max)
        a.recycle()
        layoutResource = R.layout.preference_widget_seekbar
    }

    override fun onBindView(view: View) {
        super.onBindView(view)
        val seekBar = view.findViewById<SeekBar>(R.id.seekbar)
        seekBar.setOnSeekBarChangeListener(this)
        seekBar.max = max
        seekBar.progress = progress
        seekBar.isEnabled = isEnabled
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        progress = when {
            restoreValue -> getPersistedInt(progress)
            defaultValue != null -> defaultValue as Int
            else -> 0
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any = a.getInt(index, 0)

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

    override fun onSaveInstanceState(): Parcelable {
        /*
         * Suppose a client uses this preference type without persisting. We
         * must save the instance state so it is able to, for example, survive
         * orientation changes.
         */

        val superState = super.onSaveInstanceState()
        if (isPersistent) {
            // No need to save instance state since it's persistent
            return superState
        }

        // Save the instance state
        val myState = SavedState(superState)
        myState.progress = progress
        myState.max = max
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state.javaClass != SavedState::class.java) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state)
            return
        }

        // Restore the instance state
        val myState = state as SavedState
        super.onRestoreInstanceState(myState.superState)
        progress = myState.progress
        max = myState.max
        notifyChanged()
    }

    private class SavedState : BaseSavedState {

        var progress = 0
        var max = 0

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel) : super(source) {
            progress = source.readInt()
            max = source.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(progress)
            dest.writeInt(max)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {

            override fun createFromParcel(p: Parcel) = SavedState(p)
            override fun newArray(size: Int) = arrayOfNulls<SavedState?>(size)
        }
    }
}
