package com.doctoror.particleswallpaper.data.config

object HardwareCanvasBlacklist {

    private val blackList = hashSetOf(
            "bullhead",
            "dreamlte",
            "gemini",
            "HWANE",
            "HWCMR09",
            "joan",
            "polaris",
            "wayne",
            "whyred"
    )

    fun isNotBlacklistedForLockHardwareCanvas(device: String) = !blackList.contains(device)
}
