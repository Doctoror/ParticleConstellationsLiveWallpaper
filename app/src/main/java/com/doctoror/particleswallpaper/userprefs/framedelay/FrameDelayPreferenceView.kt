package com.doctoror.particleswallpaper.userprefs.framedelay

import com.doctoror.particleswallpaper.framework.preference.SeekBarPreferenceView

interface FrameDelayPreferenceView : SeekBarPreferenceView {

    fun setFrameRate(frameRate: Int)
}
