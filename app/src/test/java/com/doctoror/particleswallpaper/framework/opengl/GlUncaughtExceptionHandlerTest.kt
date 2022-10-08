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
package com.doctoror.particleswallpaper.framework.opengl

import org.mockito.kotlin.*
import org.junit.jupiter.api.Test

class GlUncaughtExceptionHandlerTest {

    private val knownOpenglIssuesHandler: KnownOpenglIssuesHandler = mock()
    private val wrapped: Thread.UncaughtExceptionHandler = mock()

    private val underTest =
        GlUncaughtExceptionHandler(
            knownOpenglIssuesHandler,
            wrapped
        )

    @Test
    fun doesNotForwardNullToKnownOpenglIssuesHandler() {
        underTest.uncaughtException(Thread.currentThread(), null)
        verify(knownOpenglIssuesHandler, never()).handleUncaughtException(anyOrNull())
    }

    @Test
    fun alwaysForwardsNullToWrapped() {
        underTest.uncaughtException(Thread.currentThread(), null)

        verify(knownOpenglIssuesHandler, never()).handleUncaughtException(anyOrNull())
        verify(wrapped).uncaughtException(Thread.currentThread(), null)
    }

    @Test
    fun forwardsThrowableToKnownOpenglIssuesHandler() {
        val throwable = Exception()

        underTest.uncaughtException(Thread.currentThread(), throwable)

        verify(knownOpenglIssuesHandler).handleUncaughtException(throwable)
    }

    @Test
    fun forwardsThrowableToWrappedIfKnownOpenglIssuesHandlerDidNotHandle() {
        val throwable = Exception()

        underTest.uncaughtException(Thread.currentThread(), throwable)

        verify(knownOpenglIssuesHandler).handleUncaughtException(throwable)
        verify(wrapped).uncaughtException(Thread.currentThread(), throwable)
    }

    @Test
    fun doesNotForwardThrowableToWrappedIfKnownOpenglIssuesHandlerHandled() {
        val throwable = Exception()

        whenever(knownOpenglIssuesHandler.handleUncaughtException(anyOrNull())).thenReturn(true)
        underTest.uncaughtException(Thread.currentThread(), throwable)

        verify(knownOpenglIssuesHandler).handleUncaughtException(throwable)
        verify(wrapped, never()).uncaughtException(anyOrNull(), anyOrNull())
    }
}
