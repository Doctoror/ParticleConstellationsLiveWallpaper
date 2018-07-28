/*
 * Copyright 2014 Google, Inc. All rights reserved.
 * Copyright 2018 Yaroslav Mytkalyk, Inc. All rights reserved.
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
package com.doctoror.particleswallpaper.presentation.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import com.bumptech.glide.util.Synthetic
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

object TransformationUtils {

    // See #738.
    private val MODELS_REQUIRING_BITMAP_LOCK = HashSet(
            Arrays.asList(
                    // Moto X gen 2
                    "XT1085",
                    "XT1092",
                    "XT1093",
                    "XT1094",
                    "XT1095",
                    "XT1096",
                    "XT1097",
                    "XT1098",
                    // Moto G gen 1
                    "XT1031",
                    "XT1028",
                    "XT937C",
                    "XT1032",
                    "XT1008",
                    "XT1033",
                    "XT1035",
                    "XT1034",
                    "XT939G",
                    "XT1039",
                    "XT1040",
                    "XT1042",
                    "XT1045",
                    // Moto G gen 2
                    "XT1063",
                    "XT1064",
                    "XT1068",
                    "XT1069",
                    "XT1072",
                    "XT1077",
                    "XT1078",
                    "XT1079"
            )
    )

    /**
     * https://github.com/bumptech/glide/issues/738 On some devices, bitmap drawing is not thread
     * safe.
     * This lock only locks for these specific devices. For other types of devices the lock is always
     * available and therefore does not impact performance
     */
    private val BITMAP_DRAWABLE_LOCK = if (MODELS_REQUIRING_BITMAP_LOCK.contains(Build.MODEL))
        ReentrantLock()
    else
        NoLock()

    private val DEFAULT_PAINT = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)

    fun applyMatrix(inBitmap: Bitmap, targetBitmap: Bitmap, matrix: Matrix) {
        BITMAP_DRAWABLE_LOCK.lock()
        try {
            val canvas = Canvas(targetBitmap)
            canvas.drawBitmap(inBitmap, matrix, DEFAULT_PAINT)
            canvas.setBitmap(null)
        } finally {
            BITMAP_DRAWABLE_LOCK.unlock()
        }
    }
}

private class NoLock @Synthetic constructor() : Lock {

    override fun lock() {
        // do nothing
    }

    @Throws(InterruptedException::class)
    override fun lockInterruptibly() {
        // do nothing
    }

    override fun tryLock(): Boolean {
        return true
    }

    @Throws(InterruptedException::class)
    override fun tryLock(time: Long, unit: TimeUnit): Boolean {
        return true
    }

    override fun unlock() {
        // do nothing
    }

    override fun newCondition(): Condition {
        throw UnsupportedOperationException("Should not be called")
    }
}
