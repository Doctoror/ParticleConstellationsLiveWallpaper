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
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.annotation.VisibleForTesting
import com.doctoror.particleswallpaper.BuildConfig
import com.doctoror.particleswallpaper.data.ads.GoogldAdsProvider.AdLoadState.*
import com.doctoror.particleswallpaper.domain.ads.AdView
import com.doctoror.particleswallpaper.domain.ads.AdsProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.MobileAds

/**
 * Created by Yaroslav Mytkalyk on 06.06.17.
 *
 * Google Ads [AdsProvider] implementation
 */
class GoogldAdsProvider(private val context: Context) : AdsProvider {

    companion object {

        private val initializeLock = Object()
        private var mobileAdsInitialized = false
    }

    @VisibleForTesting
    enum class AdLoadState {
        IDLE, WAITING_FOR_CONNECTION, LOADING, LOADED, FAILED
    }

    @VisibleForTesting
    var adLoadState = IDLE

    private var adView: AdView? = null

    private fun initializeMobileAds() {
        synchronized(initializeLock, { initializeMobileAdsNotLocked() })
    }

    private fun initializeMobileAdsNotLocked() {
        if (!mobileAdsInitialized) {
            mobileAdsInitialized = true
            MobileAds.initialize(context, BuildConfig.AD_APP_ID)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun initializeAdView(adView: AdView) {
        adView.setAdListener(adListener)
        this.adView = adView
    }

    private fun loadAd(adView: AdView) {
        adLoadState = LOADING
        adView.loadAd()
    }

    override fun initialize(adContext: Any) {
        if (adContext !is AdView) {
            throw IllegalArgumentException(
                    ("""This AdsProvider implementation requires com.google.android.gms.ads.AdView
                    as ad context. Received: """ + adContext.javaClass).trimIndent())
        }
        initializeMobileAds()
        initializeAdView(adContext)

        adLoadState = IDLE
    }

    override fun onStart() {
        adView!!.resume()
        if (adLoadState != LOADED) {
            adLoadState = WAITING_FOR_CONNECTION
        }
        context.registerReceiver(connectivityReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        context.unregisterReceiver(connectivityReceiver)
        adView!!.pause()
    }

    override fun onDestroy() {
        adView!!.destroy()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun onAdLoaded() {
        if (adLoadState == LOADING || adLoadState == FAILED) {
            adLoadState = LOADED
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun onAdFailedToLoad() {
        if (adLoadState == LOADING) {
            adLoadState = FAILED
        }
    }

    private val adListener = object : AdListener() {

        override fun onAdLoaded() {
            super.onAdLoaded()
            this@GoogldAdsProvider.onAdLoaded()
        }

        override fun onAdFailedToLoad(p0: Int) {
            super.onAdFailedToLoad(p0)
            this@GoogldAdsProvider.onAdFailedToLoad()
        }
    }

    private val connectivityReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val adView = this@GoogldAdsProvider.adView
            if (adView != null && hasConnection()) {
                when (adLoadState) {
                    AdLoadState.WAITING_FOR_CONNECTION,
                    AdLoadState.FAILED -> loadAd(adView)
                    else -> {
                    }
                }
            }
        }

        private fun hasConnection(): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager?
            val networkInfo = connectivityManager?.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}