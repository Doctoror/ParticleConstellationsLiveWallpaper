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
package com.doctoror.particleswallpaper.data.config

import com.doctoror.particleswallpaper.data.repository.MockSettingsRepositoryFactory
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SceneConfiguratorImplTest {

    @Test
    fun testSubscription() {
        val c = SceneConfiguratorImpl()

        assertNull(c.disposables)

        c.subscribe(
                mock(),
                mock(),
                MockSettingsRepositoryFactory.create(),
                Schedulers.trampoline())

        assertNotNull(c.disposables)
        assertFalse(c.disposables!!.isDisposed)

        val disposables = c.disposables

        c.dispose()

        assertTrue(disposables!!.isDisposed)
        assertNull(c.disposables)
    }

}
