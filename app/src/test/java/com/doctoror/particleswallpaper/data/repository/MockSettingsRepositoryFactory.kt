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
package com.doctoror.particleswallpaper.data.repository

import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.mockito.Mockito.mock

/**
 * Created by Yaroslav Mytkalyk on 16.06.17.
 *
 * Used for creating mock [SettingsRepository] implementations that return stub data.
 */
object MockSettingsRepositoryFactory {

    fun create(): SettingsRepository = create(SettingsRepository::class.java)

    private fun <T : SettingsRepository> create(c: Class<T>): T {
        val result = mock(c)
        whenever(result.getNumDots()).thenReturn(Observable.just(1))
        whenever(result.getFrameDelay()).thenReturn(Observable.just(1))
        whenever(result.getStepMultiplier()).thenReturn(Observable.just(1f))
        whenever(result.getDotScale()).thenReturn(Observable.just(1f))
        whenever(result.getLineScale()).thenReturn(Observable.just(1f))
        whenever(result.getLineDistance()).thenReturn(Observable.just(1f))
        whenever(result.getParticlesColor()).thenReturn(Observable.just(1))
        whenever(result.getBackgroundUri()).thenReturn(Observable.just(NO_URI))
        whenever(result.getBackgroundColor()).thenReturn(Observable.just(1))
        return result
    }
}
