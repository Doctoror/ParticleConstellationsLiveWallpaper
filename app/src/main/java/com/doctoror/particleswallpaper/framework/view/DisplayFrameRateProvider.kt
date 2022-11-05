package com.doctoror.particleswallpaper.framework.view

import android.content.Context

class DisplayFrameRateProvider {

    /**
     * Provides frame rate of this display.
     *
     * Returns 60 if Display is not available
     */
    fun provide(context: Context): Int = try {
        context.display?.refreshRate?.toInt() ?: 60
    } catch (e: NoSuchMethodError) {
        // For some reason, NoSuchMethodError can be thrown here. Return default:
        60
    }
}
