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

import android.view.ViewGroup
import android.view.ViewParent
import com.google.android.gms.ads.AdListener

open class StubAdView : AdsProviderImpl.IAdView {

    private var mLayoutParams: ViewGroup.LayoutParams? = null

    override fun pause() {
    }

    override fun resume() {
    }

    override fun destroy() {
    }

    override fun loadAd() {
    }

    override fun setAdListener(l: AdListener) {
    }

    override fun getLayoutParams(): ViewGroup.LayoutParams? {
        return mLayoutParams
    }

    override fun setLayoutPrams(p: ViewGroup.LayoutParams?) {
        mLayoutParams = p
    }

    override fun getParent(): ViewParent? {
        return null
    }
}