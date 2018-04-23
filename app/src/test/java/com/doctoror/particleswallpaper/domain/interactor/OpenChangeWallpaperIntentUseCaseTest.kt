/*
 * Copyright (C) 2018 Yaroslav Mytkalyk
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
package com.doctoror.particleswallpaper.domain.interactor

import android.app.Activity
import android.app.Fragment
import com.doctoror.particleswallpaper.presentation.REQUEST_CODE_CHANGE_WALLPAPER
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * TODO add all tests
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class OpenChangeWallpaperIntentUseCaseTest {

    fun mockAcitvityWithPackageName(): Activity = mock {
        on(it.packageName).doReturn("com.doctoror.particleswallpaper")
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenBothActivityAndFragmentAreNull() {
        OpenChangeWallpaperIntentUseCase(null, null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crashesWhenBothActivityAndFragmentArePassed() {
        OpenChangeWallpaperIntentUseCase(mock(), mock())
    }

    @Test
    fun opensWallpaperChoserWithFragment() {
        val activity: Activity = mock {
            on(it.packageName).doReturn("SHIT")
        }
        val fragment: Fragment = mock {
            on(it.activity).doReturn(activity)
        }
        val underTest = OpenChangeWallpaperIntentUseCase(fragment = fragment)

        underTest.useCase().test()

        verify(fragment).startActivityForResult(any(), eq(REQUEST_CODE_CHANGE_WALLPAPER))
    }
}
