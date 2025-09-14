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
package com.doctoror.particleswallpaper.framework.lifecycle

import com.doctoror.particleswallpaper.app.PreferenceFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * [LifecycleOwner] [PreferenceFragment].
 * Forwards [Lifecycle.Event.ON_START] and [Lifecycle.Event.ON_STOP] lifecycle events to [LifecycleRegistry]
 */
abstract class LifecyclePreferenceFragment : PreferenceFragment(), LifecycleOwner {

    override val lifecycle = LifecycleRegistry(this)

    @Deprecated("Must declare as deprecated when overriding deprecated api")
    override fun onStart() {
        super.onStart()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    @Deprecated("Must declare as deprecated when overriding deprecated api")
    override fun onStop() {
        super.onStop()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }
}
