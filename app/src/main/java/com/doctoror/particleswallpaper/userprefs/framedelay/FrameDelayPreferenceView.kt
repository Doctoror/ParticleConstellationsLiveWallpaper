package com.doctoror.particleswallpaper.userprefs.framedelay

import com.doctoror.particleswallpaper.presentation.view.SeekBarPreferenceView

interface FrameDelayPreferenceView : SeekBarPreferenceView {

    fun setFrameRate(frameRate: Int)
}
