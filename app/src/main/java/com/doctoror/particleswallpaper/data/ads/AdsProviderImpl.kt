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

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.VisibleForTesting
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import com.doctoror.particleswallpaper.BuildConfig
import com.doctoror.particleswallpaper.domain.ads.AdsProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

/**
 * Created by Yaroslav Mytkalyk on 06.06.17.
 *
 * Google Ads [AdsProvider] implementation
 */
class AdsProviderImpl(private val context: Context) : AdsProvider {

    companion object {

        private val initializeLock = Object()
        private var mobileAdsInitialized = false
    }

    @VisibleForTesting
    enum class AdLoadState {
        IDLE, WAITING_FOR_CONNECTION, LOADING, LOADED, FAILED
    }

    @VisibleForTesting
    var adLoadState = AdLoadState.IDLE

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

    @SuppressLint("VisibleForTests") // WTF, why we need suppressing here?
    private fun initializeAdView(adView: AdView) {
        adView.adListener = newAdListener(adView)
        this.adView = adView
    }

    private fun loadAd(adView: AdView) {
        adLoadState = AdLoadState.LOADING
        adView.loadAd(AdRequest.Builder()
                .addTestDevice("1644CF0C8CE728912DC93B6C340AB453")
                .build())
    }

    override fun initialize(adContext: Any) {
        if (adContext !is AdView) {
            throw IllegalArgumentException(
                    ("""This AdsProvider implementation requires com.google.android.gms.ads.AdView
                    as ad context. Received: """ + adContext.javaClass).trimIndent())
        }
        initializeMobileAds()
        initializeAdView(adContext)

        adLoadState = AdLoadState.IDLE
    }

    override fun onStart() {
        adView!!.resume()
        if (adLoadState != AdLoadState.LOADED) {
            adLoadState = AdLoadState.WAITING_FOR_CONNECTION
        }
        context.registerReceiver(connectivityReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        context.unregisterReceiver(connectivityReceiver)
        adLoadState = AdLoadState.IDLE
        adView!!.pause()
    }

    override fun onDestroy() {
        adView!!.destroy()
    }

    @VisibleForTesting
    fun newAdListener(adView: View): AdListener {
        if (adView.layoutParams == null) {
            throw IllegalArgumentException("adView must have LayoutParams")
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) ViewResizeAdListenerKitKat(adView)
        else ViewResizeAdListener(adView)
    }

    private open inner class ViewResizeAdListener(protected val adView: View) : AdListener() {

        override fun onAdLoaded() {
            super.onAdLoaded()
            if (adLoadState == AdLoadState.LOADING
                    || adLoadState == AdLoadState.FAILED) {
                adLoadState = AdLoadState.LOADED
                if (shouldExpandAdView()) {
                    expandAdView()
                }
            }
        }

        override fun onAdFailedToLoad(p0: Int) {
            super.onAdFailedToLoad(p0)
            if (adLoadState == AdLoadState.LOADING) {
                adLoadState = AdLoadState.FAILED
            }
        }

        private fun shouldExpandAdView()
                = adView.layoutParams!!.height != ViewGroup.LayoutParams.WRAP_CONTENT

        protected open fun expandAdView() {
            val layoutParams = adView.layoutParams!!
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            adView.layoutParams = layoutParams
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private inner class ViewResizeAdListenerKitKat(adView: View) : ViewResizeAdListener(adView) {

        override fun expandAdView() {
            val adViewParent = adView.parent
            if (adViewParent is ViewGroup) {
                TransitionManager.beginDelayedTransition(adViewParent, ChangeBounds())
            }
            super.expandAdView()
        }
    }

    private val connectivityReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val adView = this@AdsProviderImpl.adView
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