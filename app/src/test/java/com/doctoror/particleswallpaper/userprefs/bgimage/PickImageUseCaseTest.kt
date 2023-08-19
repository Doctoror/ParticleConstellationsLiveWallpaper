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
package com.doctoror.particleswallpaper.userprefs.bgimage

import android.content.ActivityNotFoundException
import android.content.Intent
import com.doctoror.particleswallpaper.app.REQUEST_CODE_OPEN_DOCUMENT
import com.doctoror.particleswallpaper.framework.app.actions.StartActivityForResultAction
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PickImageUseCaseTest {

    private val underTest = PickImageUseCase()

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun opensImagePicker() {
        // Given
        val action: StartActivityForResultAction = mock()

        // When
        underTest.invoke(action)

        // Then
        val captor = argumentCaptor<Intent>()
        verify(action).startActivityForResult(captor.capture(), eq(REQUEST_CODE_OPEN_DOCUMENT))

        assertEquals(Intent.ACTION_OPEN_DOCUMENT, captor.firstValue.action)
        assertEquals(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION, captor.firstValue.flags)
        assertEquals("image/*", captor.firstValue.type)
    }

    @Test(expected = ActivityNotFoundException::class)
    fun rethrowsActivityNotFoundException() {
        // Given
        val action: StartActivityForResultAction = mock {
            on(it.startActivityForResult(any(), eq(REQUEST_CODE_OPEN_DOCUMENT)))
                .doThrow(ActivityNotFoundException())
        }

        // When
        underTest.invoke(action)
    }
}
