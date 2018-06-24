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
package com.doctoror.particleswallpaper.scheduler

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import net.rbgrn.android.glwallpaperservice.GLWallpaperService
import java.util.concurrent.TimeUnit

class GlScheduler(private val engine: GLWallpaperService.GLEngine) : Scheduler() {

    override fun createWorker(): Worker = WorkerImpl(engine)

    private class WorkerImpl(private val engine: GLWallpaperService.GLEngine) : Worker() {

        @Volatile
        private var disposed = false

        override fun isDisposed() = disposed

        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            if (delay != 0L) {
                throw IllegalArgumentException("Delays are not supported")
            }

            if (disposed) {
                return Disposables.disposed()
            }

            val runnable = DisposableRunnable(run)
            engine.queueEvent(runnable)

            if (disposed) {
                return Disposables.disposed()
            }

            return runnable
        }

        override fun dispose() {
            disposed = true
        }

        private class DisposableRunnable(private val wrapped: Runnable) : Disposable, Runnable {

            private var disposed = false

            override fun dispose() {
                disposed = true
            }

            override fun isDisposed() = disposed

            override fun run() {
                if (!disposed) {
                    try {
                        wrapped.run()
                    } catch (t: Throwable) {
                        RxJavaPlugins.onError(t)
                    }
                }
            }
        }
    }
}
