package com.doctoror.particleswallpaper.framework.app

import com.doctoror.particleswallpaper.framework.app.HardwareCanvasBlacklist
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Workaround for https://issuetracker.google.com/issues/70259031
 */
class HardwareCanvasBlacklistTest {

    @Test
    fun randomDeviceNotBlacklisted() {
        assertFalse(HardwareCanvasBlacklist.isBlacklistedForLockHardwareCanvas(
                "hammerhead", "hammerhead"))
    }

    @Test
    fun bullheadBlacklisted() {
        assertTrue(HardwareCanvasBlacklist.isBlacklistedForLockHardwareCanvas(
                "bullhead", "bullhead"))
    }
}
