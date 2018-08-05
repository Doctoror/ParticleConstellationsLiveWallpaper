package com.doctoror.particleswallpaper.data.config

object HardwareCanvasBlacklist {

    private val blackList = hashSetOf(
            "bullhead",
            "degaswifiue",
            "dream2qltesq",
            "dreamlte",
            "dreamlteks",
            "F8332",
            "gemini",
            "grandneove3g",
            "greatlte",
            "hero2lte",
            "heroqlteaio",
            "HWANE",
            "HWCMR09",
            "HWDRA-M",
            "HWSTF",
            "joan",
            "lithium",
            "nash",
            "OnePlus5T",
            "OnePlus6",
            "P450L10",
            "polaris",
            "sagit",
            "scorpio",
            "taimen",
            "TECNO-CA7",
            "tissot_sprout",
            "vince",
            "wayne",
            "whyred",
            "ysl"
    )

    fun isBlacklistedForLockHardwareCanvas(device: String, product: String) =
            blackList.contains(device) || blackList.contains(product)
}
