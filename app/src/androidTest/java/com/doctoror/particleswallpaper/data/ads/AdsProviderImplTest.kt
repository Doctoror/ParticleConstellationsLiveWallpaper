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
import android.support.test.InstrumentationRegistry
import android.view.View
import android.view.ViewGroup
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import kotlin.test.assertEquals

import com.doctoror.particleswallpaper.data.ads.AdsProviderImpl.AdLoadState
import com.doctoror.particleswallpaper.data.ads.AdsProviderImpl.AdLoadState.*
import com.google.android.gms.ads.AdListener

/**
 * Created by Yaroslav Mytkalyk on 06.06.17.
 *
 * [AdsProviderImpl] test
 */
class AdsProviderImplTest {

    private fun newAdsProviderWithMockContext() = AdsProviderImpl(mock(Context::class.java))

    private fun newAdListenerWithValidView(adsProvider: AdsProviderImpl): AdListener {
        val view = View(InstrumentationRegistry.getContext())
        view.layoutParams = ViewGroup.LayoutParams(0, 0)
        return adsProvider.newAdListener(view)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWrongContextThrowsIllegalArgumentException() {
        AdsProviderImpl(InstrumentationRegistry.getContext())
                .initialize(Object())
    }

    @Test
    fun testReceiverRegisteredOnStart() {
        val context = mock(Context::class.java)

        val adsProvider = AdsProviderImpl(context)
        adsProvider.onStart()

        verify(context, times(1)).registerReceiver(
                ArgumentMatchers.any(BroadcastReceiver::class.java),
                ArgumentMatchers.any(IntentFilter::class.java))
    }

    @Test
    fun testStateWaitingForConnectionOnStart() {
        val adsProvider = AdsProviderImpl(mock(Context::class.java))
        adsProvider.onStart()

        assertEquals(WAITING_FOR_CONNECTION,
                adsProvider.adLoadState)
    }

    @Test
    fun testStateIdleOnStop() {
        val adsProvider = AdsProviderImpl(mock(Context::class.java))
        adsProvider.onStop()

        assertEquals(IDLE, adsProvider.adLoadState)
    }

    @Test
    fun testReceiverUnregisteredOnStop() {
        val context = mock(Context::class.java)

        val adsProvider = AdsProviderImpl(context)
        adsProvider.onStop()

        verify(context, times(1)).unregisterReceiver(
                ArgumentMatchers.any(BroadcastReceiver::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAdViewWithNoLayoutParamsNotAcceptable() {
        val adsProvider = AdsProviderImpl(mock(Context::class.java))
        adsProvider.newAdListener(View(InstrumentationRegistry.getContext()))
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
        val adsProvider = newAdsProviderWithMockContext()
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
        val adsProvider = newAdsProviderWithMockContext()
        adsProvider.adLoadState = loadWithState

        val adListener = newAdListenerWithValidView(adsProvider)
        adListener.onAdFailedToLoad(0)

        assertEquals(expectedStateWhenFaildToLoad, adsProvider.adLoadState)
    }

    @Test
    fun testAdViewLayoutHeightChangedWhenAdLoaded() {
        val adsProvider = newAdsProviderWithMockContext()
        adsProvider.adLoadState = LOADING

        val view = View(InstrumentationRegistry.getContext())
        view.layoutParams = ViewGroup.LayoutParams(0, 0)

        val adListener = adsProvider.newAdListener(view)
        adListener.onAdLoaded()

        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, view.layoutParams.height)
    }
}
