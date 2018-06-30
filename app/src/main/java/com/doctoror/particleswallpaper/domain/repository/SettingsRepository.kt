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
package com.doctoror.particleswallpaper.domain.repository

import io.reactivex.Observable

const val NO_URI = ""

/**
 * The configuration repository
 */
interface SettingsRepository {

    fun getBackgroundColor(): Observable<Int>
    fun getBackgroundUri(): Observable<String>
    fun getDotScale(): Observable<Float>
    fun getFrameDelay(): Observable<Int>
    fun getLineDistance(): Observable<Float>
    fun getLineScale(): Observable<Float>
    fun getNumDots(): Observable<Int>
    fun getNumSamples(): Observable<Int>
    fun getParticlesColor(): Observable<Int>
    fun getStepMultiplier(): Observable<Float>
}
