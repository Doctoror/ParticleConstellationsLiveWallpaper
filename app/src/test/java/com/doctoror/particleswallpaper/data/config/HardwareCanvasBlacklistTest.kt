package com.doctoror.particleswallpaper.data.config

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Workaround for https://issuetracker.google.com/issues/70259031
 */
class HardwareCanvasBlacklistTest {

    @Test
    fun randomDeviceNotBlacklisted() {
        assertTrue(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("hammerhead"))
    }

    @Test
    fun bullheadBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("bullhead"))
    }

    @Test
    fun dreamlteBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("dreamlte"))
    }

    @Test
    fun geminiBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("gemini"))
    }

    @Test
    fun hwaneBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("HWANE"))
    }

    @Test
    fun joanBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("joan"))
    }

    @Test
    fun polarisBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("polaris"))
    }

    @Test
    fun whyredBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("whyred"))
    }
}
