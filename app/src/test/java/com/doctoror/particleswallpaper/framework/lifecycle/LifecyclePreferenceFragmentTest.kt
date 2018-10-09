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
package com.doctoror.particleswallpaper.framework.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.FragmentController

@RunWith(RobolectricTestRunner::class)
class LifecyclePreferenceFragmentTest {

    private val underTest = TestLifecycleFragment()
    private val underTestController = FragmentController.of(underTest)

    private val testObserver = object : LifecycleObserver {

        var onStartCount = 0
        var onStopCount = 0

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            onStartCount++
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            onStopCount++
        }
    }

    @Before
    fun setup() {
        underTest.lifecycle.addObserver(testObserver)
    }

    @After
    fun tearDown() {
        underTest.lifecycle.removeObserver(testObserver)
        StandAloneContext.stopKoin()
    }

    @Test
    fun notifiesOnStartLifecycleEvent() {
        // When
        underTestController
            .create()
            .start()

        // Then
        assertEquals(1, testObserver.onStartCount)
    }

    @Test
    fun notifiesOnStopLifecycleEvent() {
        // When
        underTestController
            .create()
            .start()
            .stop()

        // Then
        assertEquals(1, testObserver.onStopCount)
    }

    class TestLifecycleFragment : LifecyclePreferenceFragment()
}
