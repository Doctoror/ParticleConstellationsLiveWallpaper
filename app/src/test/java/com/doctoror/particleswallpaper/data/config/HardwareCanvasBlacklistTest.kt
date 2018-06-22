package com.doctoror.particleswallpaper.data.config

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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
    fun geminiBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("gemini"))
    }

    @Test
    fun joanBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("joan"))
    }

    @Test
    fun whyredBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isNotBlacklistedForLockHardwareCanvas("whyred"))
    }
}
