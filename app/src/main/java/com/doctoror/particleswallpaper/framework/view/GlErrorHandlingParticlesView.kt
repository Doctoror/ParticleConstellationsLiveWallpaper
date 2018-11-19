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
package com.doctoror.particleswallpaper.framework.view

import android.annotation.SuppressLint
import android.content.Context
import com.doctoror.particlesdrawable.opengl.GlParticlesView
import com.doctoror.particlesdrawable.opengl.chooser.EGLConfigChooserCallback
import com.doctoror.particlesdrawable.opengl.util.GLErrorChecker
import com.doctoror.particleswallpaper.framework.di.inject
import com.doctoror.particleswallpaper.framework.opengl.KnownOpenglIssuesHandler
import javax.microedition.khronos.opengles.GL10

@SuppressLint("ViewConstructor") // used only from ParticlesViewGenerator
class GlErrorHandlingParticlesView(
    context: Context,
    numSamples: Int,
    eglConfigChooserCallback: EGLConfigChooserCallback?
) : GlParticlesView(context, null, numSamples, eglConfigChooserCallback) {

    private val knownOpenglIssuesHandler: KnownOpenglIssuesHandler by inject(
        context = context
    )

    init {
        GLErrorChecker.setShouldCheckGlError(true)
    }

    override fun onDrawFrame(gl10: GL10) {
        GLErrorChecker.setShouldCheckGlError(false)

        super.onDrawFrame(gl10)
        knownOpenglIssuesHandler.handleGlError("GlErrorHandlingParticlesView.onDrawFrame")
    }
}
