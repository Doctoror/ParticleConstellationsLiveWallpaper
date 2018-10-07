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
package com.doctoror.particleswallpaper.userprefs.data

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable

/**
 * Used for creating mock [SceneSettings] implementations that return stub data.
 */
object MockSceneSettingsFactory {

    fun create(): SceneSettings {
        val result: SceneSettings = mock()
        whenever(result.observeBackgroundColor()).thenReturn(Observable.just(1))
        whenever(result.observeBackgroundUri()).thenReturn(Observable.just(NO_URI))
        whenever(result.observeDensity()).thenReturn(Observable.just(1))
        whenever(result.observeFrameDelay()).thenReturn(Observable.just(1))
        whenever(result.observeLineLength()).thenReturn(Observable.just(1f))
        whenever(result.observeLineScale()).thenReturn(Observable.just(1f))
        whenever(result.observeParticleColor()).thenReturn(Observable.just(1))
        whenever(result.observeParticleScale()).thenReturn(Observable.just(1f))
        whenever(result.observeSpeedFactor()).thenReturn(Observable.just(1f))
        return result
    }
}
