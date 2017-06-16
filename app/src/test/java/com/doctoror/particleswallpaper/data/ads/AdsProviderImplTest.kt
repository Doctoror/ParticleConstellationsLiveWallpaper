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
package com.doctoror.particleswallpaper.data.ads

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.view.ViewGroup
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import kotlin.test.assertEquals

import com.doctoror.particleswallpaper.data.ads.AdsProviderImpl.IAdView
import com.doctoror.particleswallpaper.data.ads.AdsProviderImpl.AdLoadState
import com.doctoror.particleswallpaper.data.ads.AdsProviderImpl.AdLoadState.*
import com.google.android.gms.ads.AdListener

/**
 * Created by Yaroslav Mytkalyk on 06.06.17.
 *
 * [AdsProviderImpl] test
 */
class AdsProviderImplTest {

    private fun newAdsProvider(context: Context = mock(Context::class.java))
            = AdsProviderImpl(context)

    private fun newAdsProviderWithMockAdView(context: Context = mock(Context::class.java)):
            Pair<AdsProviderImpl, IAdView> {
        val adsProvider = newAdsProvider(context)
        val adView = mock(IAdView::class.java)
        `when`(adView.getLayoutParams()).thenReturn(ViewGroup.LayoutParams(0, 0))
        adsProvider.initializeAdView(adView)
        return Pair(adsProvider, adView)
    }

    private fun newAdViewWithMutableLayoutParams(): IAdView {
        val view = StubAdViewWithLayoutParams()
        view.setLayoutPrams(ViewGroup.LayoutParams(0, 0))
        return view
    }

    private fun newAdListenerWithValidView(adsProvider: AdsProviderImpl): AdListener {
        return adsProvider.newAdListener(newAdViewWithMutableLayoutParams())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWrongContextThrowsIllegalArgumentException() {
        newAdsProvider().initialize(Object())
    }

    @Test
    fun testReceiverRegisteredOnStart() {
        val context = mock(Context::class.java)

        val adsProvider = newAdsProviderWithMockAdView(context).first
        adsProvider.onStart()

        verify(context, times(1)).registerReceiver(
                ArgumentMatchers.any(BroadcastReceiver::class.java),
                ArgumentMatchers.any(IntentFilter::class.java))
    }

    @Test
    fun testStateWaitingForConnectionOnStart() {
        val adsProvider = newAdsProviderWithMockAdView().first
        adsProvider.onStart()

        assertEquals(WAITING_FOR_CONNECTION,
                adsProvider.adLoadState)
    }

    @Test
    fun testStateIdleOnStop() {
        val adsProvider = newAdsProviderWithMockAdView().first
        adsProvider.onStop()

        assertEquals(IDLE, adsProvider.adLoadState)
    }

    @Test
    fun testReceiverUnregisteredOnStop() {
        val context = mock(Context::class.java)

        val adsProvider = newAdsProviderWithMockAdView(context).first
        adsProvider.onStop()

        verify(context, times(1)).unregisterReceiver(
                ArgumentMatchers.any(BroadcastReceiver::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAdViewWithNoLayoutParamsNotAcceptable() {
        val adsProvider = newAdsProvider()
        adsProvider.newAdListener(StubAdViewWithLayoutParams())
    }

    @Test
    fun testStateNotChangedWhenLoadedWhileStateIdle() {
        testStateWhenLoadedWithState(
                IDLE,
                IDLE)
    }

    @Test
    fun testStateNotChangedWhenLoadedWhileStateLoaded() {
        testStateWhenLoadedWithState(
                LOADED,
                LOADED)
    }

    @Test
    fun testStateNotChangedWhenLoadedWhileStateWaitingForConnection() {
        testStateWhenLoadedWithState(
                WAITING_FOR_CONNECTION,
                WAITING_FOR_CONNECTION)
    }

    @Test
    fun testStateLoadedWhenLoadedWhileStateLoading() {
        testStateWhenLoadedWithState(
                LOADING,
                LOADED)
    }

    @Test
    fun testStateLoadedWhenLoadedWhileStateFailed() {
        testStateWhenLoadedWithState(
                FAILED,
                LOADED)
    }

    private fun testStateWhenLoadedWithState(
            loadWithState: AdLoadState,
            expectedStateWhenLoaded: AdLoadState) {
        val adsProvider = newAdsProvider()
        adsProvider.adLoadState = loadWithState

        val adListener = newAdListenerWithValidView(adsProvider)
        adListener.onAdLoaded()

        assertEquals(expectedStateWhenLoaded, adsProvider.adLoadState)
    }

    @Test
    fun testStateNotChangedWhenFailedWithStateIdle() {
        testStateWhenFailedWithState(
                IDLE,
                IDLE)
    }

    @Test
    fun testStateNotChangedWhenFailedWithStateWaitingForConnection() {
        testStateWhenFailedWithState(
                WAITING_FOR_CONNECTION,
                WAITING_FOR_CONNECTION)
    }

    @Test
    fun testStateWhenFailedWithStateLoading() {
        testStateWhenFailedWithState(
                LOADING,
                FAILED)
    }

    @Test
    fun testStateNotChangedWhenFailedWithStateLoaded() {
        testStateWhenFailedWithState(
                LOADED,
                LOADED)
    }

    @Test
    fun testStateNotChangedWhenFailedWithStateFailed() {
        testStateWhenFailedWithState(
                FAILED,
                FAILED)
    }

    private fun testStateWhenFailedWithState(
            loadWithState: AdLoadState,
            expectedStateWhenFaildToLoad: AdLoadState) {
        val adsProvider = newAdsProvider()
        adsProvider.adLoadState = loadWithState

        val adListener = newAdListenerWithValidView(adsProvider)
        adListener.onAdFailedToLoad(0)

        assertEquals(expectedStateWhenFaildToLoad, adsProvider.adLoadState)
    }

    @Test
    fun testAdViewLayoutHeightChangedWhenAdLoaded() {
        val adsProvider = newAdsProvider()
        adsProvider.adLoadState = LOADING

        val view = newAdViewWithMutableLayoutParams()

        val adListener = adsProvider.newAdListener(view)
        adListener.onAdLoaded()

        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, view.getLayoutParams()!!.height)
    }

    @Test
    fun testAdViewResumeCalledOnStart() {
        val stuff = newAdsProviderWithMockAdView()
        stuff.first.onStart()
        verify(stuff.second, times(1)).resume()
    }

    @Test
    fun testAdViewPauseCalledOnStop() {
        val stuff = newAdsProviderWithMockAdView()
        stuff.first.onStop()
        verify(stuff.second, times(1)).pause()
    }

    @Test
    fun testAdViewDestroyCalledOnDestroy() {
        val stuff = newAdsProviderWithMockAdView()
        stuff.first.onDestroy()
        verify(stuff.second, times(1)).destroy()
    }
}
