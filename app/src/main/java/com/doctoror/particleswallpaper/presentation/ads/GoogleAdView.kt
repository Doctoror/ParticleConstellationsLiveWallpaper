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

import android.annotation.TargetApi
import android.os.Build
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.doctoror.particleswallpaper.domain.ads.AdView as AdViewInterface

/**
 * Created by Yaroslav Mytkalyk on 16.06.17.
 *
 * Google [com.doctoror.particleswallpaper.domain.ads.AdView] implementation.
 */
class GoogleAdView(private val adView: AdView) : AdViewInterface {

    private val viewExpander = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        ViewExpanderKitKat()
    } else {
        ViewExpanderImpl()
    }

    init {
        adView.adListener = AdListenerInternal()
    }

    private var adListenerExternal: AdListener? = null

    override fun pause() = adView.pause()
    override fun resume() = adView.resume()
    override fun destroy() = adView.destroy()

    override fun loadAd() {
        if (adView.layoutParams == null) {
            throw IllegalStateException("AdView must have LayoutParams set")
        }
        adView.loadAd(AdRequest.Builder()
                .addTestDevice("1644CF0C8CE728912DC93B6C340AB453")
                .build())
    }

    override fun setAdListener(l: AdListener) {
        adListenerExternal = l
    }

    fun onAdLoaded() {
        viewExpander.expandView()
    }

    private inner class AdListenerInternal : AdListener() {

        override fun onAdLoaded() {
            super.onAdLoaded()
            adListenerExternal?.onAdLoaded()
            this@GoogleAdView.onAdLoaded()
        }

        override fun onAdClicked() {
            super.onAdClicked()
            adListenerExternal?.onAdClicked()
        }

        override fun onAdClosed() {
            super.onAdClosed()
            adListenerExternal?.onAdClosed()
        }

        override fun onAdFailedToLoad(p0: Int) {
            super.onAdFailedToLoad(p0)
            adListenerExternal?.onAdFailedToLoad(p0)
        }

        override fun onAdImpression() {
            super.onAdImpression()
            adListenerExternal?.onAdImpression()
        }

        override fun onAdLeftApplication() {
            super.onAdLeftApplication()
            adListenerExternal?.onAdLeftApplication()
        }

        override fun onAdOpened() {
            super.onAdOpened()
            adListenerExternal?.onAdOpened()
        }
    }

    private interface ViewExpander {
        fun expandView()
    }

    private open inner class ViewExpanderImpl : ViewExpander {

        override fun expandView() {
            if (shouldExpandAdView()) {
                expandAdView()
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
    private inner class ViewExpanderKitKat : ViewExpanderImpl() {

        override fun expandAdView() {
            val adViewParent = adView.parent
            if (adViewParent is ViewGroup) {
                val t = ChangeBounds()
                // This listener is a workaround around the post-transition ListView issue
                // after transition, the ListView is not clickable until scrolled or requestLayout()
                // called. This dirty motherfucker fixes the issue, while we can come up with a
                // better idea
                t.addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition?) {
                        adViewParent.findViewById(android.R.id.list).requestLayout()
                    }
                })
                TransitionManager.beginDelayedTransition(adViewParent, t)
            }
            super.expandAdView()
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private open class TransitionListenerAdapter : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition?) {
        }

        override fun onTransitionResume(transition: Transition?) {
        }

        override fun onTransitionPause(transition: Transition?) {
        }

        override fun onTransitionCancel(transition: Transition?) {
        }

        override fun onTransitionStart(transition: Transition?) {
        }
    }
}