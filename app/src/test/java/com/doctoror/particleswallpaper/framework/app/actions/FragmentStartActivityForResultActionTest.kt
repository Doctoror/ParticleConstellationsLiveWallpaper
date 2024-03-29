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
package com.doctoror.particleswallpaper.framework.app.actions

import android.app.Fragment
import android.content.Intent
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FragmentStartActivityForResultActionTest {

    private val fragment: Fragment = mock()
    private val underTest = FragmentStartActivityForResultAction(fragment)

    @Test
    fun startsActivityForResult() {
        // Given
        val intent = Intent()
        val requestCode = 1

        // When
        underTest.startActivityForResult(intent, requestCode)

        // Then
        verify(fragment).startActivityForResult(intent, requestCode)
    }
}
