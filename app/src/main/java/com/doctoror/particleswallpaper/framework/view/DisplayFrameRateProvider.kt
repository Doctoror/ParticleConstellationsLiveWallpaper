package com.doctoror.particleswallpaper.framework.view

import android.content.Context
import androidx.core.content.ContextCompat

class DisplayFrameRateProvider {

    /**
     * Provides frame rate of this display.
     *
     * Returns 60 if Display is not available
     */
    fun provide(context: Context): Int = try {
        ContextCompat.getDisplayOrDefault(context).refreshRate.toInt()
    } catch (e: NoSuchMethodError) {
        // For some reason, NoSuchMethodError can be thrown here. Return default:
        60
    }
}
