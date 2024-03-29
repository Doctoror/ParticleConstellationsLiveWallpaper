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
package com.doctoror.particleswallpaper.userprefs.performancetips

import org.junit.Test
import org.mockito.kotlin.mock

class PerformanceTipsPreferencePresenterTest {

    private val view: PerformanceTipsPreferenceView = mock()
    private val underTest = PerformanceTipsPreferencePresenter(view)

    @Test
    fun showsDialogOnClick() {
        // When
        underTest.onClick()

        // Then
        view.showDialog()
    }
}
