/**
 * License for everything not in third_party and not otherwise marked:
 *
 * Copyright 2018 Yaroslav Mytkalyk.
 * Copyright 2014 Google, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY GOOGLE, INC. ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GOOGLE, INC. OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Google, Inc.
 */
package com.doctoror.particleswallpaper.framework.glide

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.util.Util

/**
 * Like [com.bumptech.glide.request.target.SimpleTarget], but forces to implement [onLoadCleared].
 */
abstract class SimpleTarget2<R : Any>(
    private val width: Int,
    private val height: Int
) : Target<R> {

    private var request: Request? = null

    override fun onLoadStarted(placeholder: Drawable?) {
        // Do nothing.
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        // Do nothing.
    }

    override fun getSize(cb: SizeReadyCallback) {
        if (!Util.isValidDimensions(width, height)) {
            throw IllegalArgumentException(
                """
                    "Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given width:
                    $width and height: $height, either provide dimensions in the constructor or
                    call override()
                    """.trimIndent()
            )
        }
        cb.onSizeReady(width, height)
    }

    override fun getRequest() = request

    override fun onStop() {
        // Do nothing.
    }

    override fun setRequest(request: Request?) {
        this.request = request
    }

    override fun removeCallback(cb: SizeReadyCallback) {
        // Do nothing.
    }

    override fun onStart() {
        // Do nothing.
    }

    override fun onDestroy() {
        // Do nothing.
    }
}
