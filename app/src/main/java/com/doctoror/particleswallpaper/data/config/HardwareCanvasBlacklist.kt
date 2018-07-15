package com.doctoror.particleswallpaper.data.config

object HardwareCanvasBlacklist {

    private val blackList = hashSetOf(
            "bullhead",
            "degaswifiue",
            "dreamlte",
            "gemini",
            "grandneove3g",
            "heroqlteaio",
            "HWANE",
            "HWCMR09",
            "HWSTF",
            "joan",
            "lithium",
            "nash",
            "OnePlus6",
            "P450L10",
            "polaris",
            "sagit",
            "scorpio",
            "taimen",
            "TECNO-CA7",
            "vince",
            "wayne",
            "whyred",
            "ysl"
    )

    fun isNotBlacklistedForLockHardwareCanvas(device: String) = !blackList.contains(device)
}
