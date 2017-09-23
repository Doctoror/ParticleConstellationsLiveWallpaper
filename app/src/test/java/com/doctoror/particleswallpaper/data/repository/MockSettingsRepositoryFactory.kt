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

import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.NO_URI
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import io.reactivex.Observable

import org.mockito.Mockito.*

/**
 * Created by Yaroslav Mytkalyk on 16.06.17.
 *
 * Used for creating mock [SettingsRepository] implementations that return stub data.
 */
object MockSettingsRepositoryFactory {

    fun create(): SettingsRepository
            = create(SettingsRepository::class.java)

    fun createMutable(): MutableSettingsRepository
            = create(MutableSettingsRepository::class.java)

    private fun <T : SettingsRepository> create(c: Class<T>): T {
        val result = mock(c)
        `when`(result.getNumDots()).thenReturn(Observable.just(1))
        `when`(result.getFrameDelay()).thenReturn(Observable.just(1))
        `when`(result.getStepMultiplier()).thenReturn(Observable.just(1f))
        `when`(result.getDotScale()).thenReturn(Observable.just(1f))
        `when`(result.getLineScale()).thenReturn(Observable.just(1f))
        `when`(result.getLineDistance()).thenReturn(Observable.just(1f))
        `when`(result.getParticlesColor()).thenReturn(Observable.just(1))
        `when`(result.getBackgroundUri()).thenReturn(Observable.just(NO_URI))
        `when`(result.getBackgroundColor()).thenReturn(Observable.just(1))
        return result
    }
}