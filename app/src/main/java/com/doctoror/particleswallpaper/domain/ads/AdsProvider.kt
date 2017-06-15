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
package com.doctoror.particleswallpaper.domain.ads

/**
 * Created by Yaroslav Mytkalyk on 06.06.17.
 *
 * Provides ads. Call [initialize] to initialize the ad with the given context.
 */
interface AdsProvider {

    /**
     * Initialize the ad for the given context
     *
     * @param adContext the view that shows the ad
     */
    fun initialize(adContext: Any)

    fun onStart()

    fun onStop()

    fun onDestroy()
}