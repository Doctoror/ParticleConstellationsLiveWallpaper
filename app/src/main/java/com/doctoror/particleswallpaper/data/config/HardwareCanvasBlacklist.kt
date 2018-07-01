package com.doctoror.particleswallpaper.data.config

object HardwareCanvasBlacklist {

    private val blackList = hashSetOf(
            "bullhead",
            "dreamlte",
            "gemini",
            "HWANE",
            "joan",
            "polaris",
            "whyred"
    )

    fun isNotBlacklistedForLockHardwareCanvas(device: String) = !blackList.contains(device)
}
