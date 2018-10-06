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
import com.doctoror.particleswallpaper.app.REQUEST_CODE_GET_CONTENT
import com.doctoror.particleswallpaper.framework.app.actions.StartActivityForResultAction
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class PickImageGetContentUseCaseTest {

    private val underTest = PickImageGetContentUseCase()

    @Test
    fun picksByGetContentChooser() {
        // Given
        val action: StartActivityForResultAction = mock()

        // When
        underTest.invoke(action)

        // Then
        val captor = argumentCaptor<Intent>()
        verify(action).startActivityForResult(captor.capture(), eq(REQUEST_CODE_GET_CONTENT))

        val intent = captor.firstValue
        assertEquals(Intent.ACTION_CHOOSER, intent.action)
        assertValidGetContentIntent(intent.getParcelableExtra(Intent.EXTRA_INTENT))
    }

    @Test
    fun picksByGetContentDirectWhenChooserFailed() {
        // Given
        val action: StartActivityForResultAction = mock {
            var thrown = false
            on(it.startActivityForResult(any(), eq(REQUEST_CODE_GET_CONTENT)))
                    .doAnswer {
                        if (!thrown) {
                            thrown = true
                            throw ActivityNotFoundException()
                        } else {
                            Unit
                        }
                    }
        }

        // When
        underTest.invoke(action)

        // Then
        val captor = argumentCaptor<Intent>()
        verify(action, times(2))
                .startActivityForResult(captor.capture(), eq(REQUEST_CODE_GET_CONTENT))

        assertValidGetContentIntent(captor.secondValue)
    }

    private fun assertValidGetContentIntent(intent: Intent) {
        assertEquals(Intent.ACTION_GET_CONTENT, intent.action)
        assertEquals("image/*", intent.type)
        assertTrue(intent.getBooleanExtra(Intent.EXTRA_LOCAL_ONLY, false))
        assertEquals(Intent.FLAG_GRANT_READ_URI_PERMISSION, intent.flags)
        assertTrue(intent.hasCategory(Intent.CATEGORY_OPENABLE))
    }
}
