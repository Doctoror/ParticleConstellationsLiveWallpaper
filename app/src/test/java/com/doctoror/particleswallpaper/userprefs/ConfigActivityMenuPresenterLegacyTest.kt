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
package com.doctoror.particleswallpaper.userprefs

import org.junit.Assert.assertFalse
import org.junit.Test
import org.mockito.kotlin.mock

class ConfigActivityMenuPresenterLegacyTest {

    private val underTest = ConfigActivityMenuPresenterLegacy()

    @Test
    fun returnsFalseOnCreateOptionsMenu() {
        val result = underTest.onCreateOptionsMenu(mock())
        assertFalse(result)
    }

    @Test
    fun returnsFalseOnOptionsItemSelected() {
        val result = underTest.onOptionsItemSelected(mock())
        assertFalse(result)
    }
}
