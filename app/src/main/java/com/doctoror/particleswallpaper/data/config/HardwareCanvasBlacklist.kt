package com.doctoror.particleswallpaper.data.config

object HardwareCanvasBlacklist {

    private val blackList = hashSetOf(
            "bullhead",
            "dreamlte",
            "gemini",
            "joan",
            "whyred"
    )

    fun isNotBlacklistedForLockHardwareCanvas(device: String) = !blackList.contains(device)
}
