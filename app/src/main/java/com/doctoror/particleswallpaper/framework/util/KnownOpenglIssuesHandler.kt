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
package com.doctoror.particleswallpaper.framework.util

import android.opengl.GLES20
import com.doctoror.particlesdrawable.opengl.GlException

class KnownOpenglIssuesHandler(
    private val openglDisabler: OpenglDisabler
) {

    fun handle(tag: String) {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            // There are reports of GL_OUT_OF_MEMORY on glUseProgram for unknown reasons.
            // Since there is not enough data to handle it, disable opengl support.
            // https://bugs.chromium.org/p/webrtc/issues/detail?id=8154
            // https://developer.samsung.com/forum/thread/bug-in-opengl-driver--samsung-opengl-shader-linking-with-out_of_memory-on-sm-g930fd/201/307111
            if (error == GLES20.GL_OUT_OF_MEMORY) {
                openglDisabler.disableOpenGl()
            } else {
                throw GlException(error, tag)
            }
        }
    }
}
