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
package com.doctoror.particleswallpaper.data.ads

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.test.InstrumentationRegistry
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import kotlin.test.assertEquals

/**
 * Created by Yaroslav Mytkalyk on 06.06.17.
 *
 * [AdsProviderImpl] test
 *
 * TODO add more tests here
 */
class AdsProviderImplTest {

    @Test(expected = IllegalArgumentException::class)
    fun testWrongContextThrowsIllegalArgumentException() {
        AdsProviderImpl(InstrumentationRegistry.getContext())
                .initialize(Object())
    }

    @Test
    fun testReceiverRegisteredOnStart() {
        val context = mock(Context::class.java)

        val adsProvider = AdsProviderImpl(context)
        adsProvider.onStart()

        assertEquals(AdsProviderImpl.AdLoadState.WAITING_FOR_CONNECTION,
                adsProvider.adLoadState)

        verify(context, times(1)).registerReceiver(
                ArgumentMatchers.any(BroadcastReceiver::class.java),
                ArgumentMatchers.any(IntentFilter::class.java))
    }
}
