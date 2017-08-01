/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particleswallpaper.presentation.ads

import android.view.ViewGroup
import com.google.android.gms.ads.AdView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * [GoogleAdView] test
 */
@RunWith(RobolectricTestRunner::class)
class GoogleAdViewTest {

    private fun newAdView() = AdView(RuntimeEnvironment.application)

    @Test(expected = IllegalStateException::class)
    fun testDoesNotLoadAdWithNoLayoutParams() {
        GoogleAdView(newAdView()).loadAd()
    }

    @Test
    fun testAdViewLayoutHeightChangedWhenAdLoaded() {
        val adView = newAdView()
        adView.layoutParams = ViewGroup.LayoutParams(0, 0)

        val gAdView = GoogleAdView(adView)
        gAdView.onAdLoaded()

        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, adView.layoutParams!!.height)
    }
}
