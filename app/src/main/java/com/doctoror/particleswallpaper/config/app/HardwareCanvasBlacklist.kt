package com.doctoror.particleswallpaper.config.app

object HardwareCanvasBlacklist {

    private val blackList = hashSetOf(
            "beryllium",
            "bullhead",
            "chiron",
            "degaswifiue",
            "dipper",
            "dream2lte",
            "dream2qltesq",
            "dreamlte",
            "dreamlteks",
            "elsa",
            "F8332",
            "G3123",
            "gemini",
            "grandneove3g",
            "grandpplte",
            "greatlte",
            "hero2lte",
            "heroqlteaio",
            "HWANE",
            "HWCMR09",
            "HWDRA-M",
            "HWDRA-MG",
            "HWMHA",
            "HWPIC",
            "HWSTF",
            "jasmine_sprout",
            "joan",
            "lithium",
            "mata",
            "nash",
            "nora_8917",
            "OnePlus5T",
            "OnePlus6",
            "P450L10",
            "PLE",
            "polaris",
            "sagit",
            "sakura",
            "scorpio",
            "sirius",
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
