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
package io.reactivex.subjects

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException

class AsyncInitialValueBehaviorSubjectTest {

    private val wrapped: BehaviorSubject<Int> = mock()

    private val initialValueSupplier = { 666 }

    private val underTest = AsyncInitialValueBehaviorSubject(initialValueSupplier, wrapped)

    @Test
    fun forwardsOnCompleteToWrapped() {
        underTest.onComplete()

        verify(wrapped).onComplete()
    }

    @Test
    fun forwardsOnErrorToWrapped() {
        val throwable = IOException()

        underTest.onError(throwable)

        verify(wrapped).onError(throwable)
    }

    @Test
    fun forwardsOnNextToWrapped() {
        underTest.onNext(1)

        verify(wrapped).onNext(1)
    }

    @Test
    fun forwardsOnSubscribeToWrapped() {
        val disposable: Disposable = mock()

        underTest.onSubscribe(disposable)

        verify(wrapped).onSubscribe(disposable)
    }

    @Test
    fun forwardsSubscribeActualToWrapped() {
        val observer: Observer<Int> = mock()

        invokeSubscribeActual(underTest, observer)

        verify(wrapped).subscribeActual(observer)
    }

    @Test
    fun returnsHasCompleteFromWrapped() {
        whenever(wrapped.hasComplete()).thenReturn(true)

        assertTrue(underTest.hasComplete())
        verify(wrapped).hasComplete()
    }

    @Test
    fun returnsHasObserversFromWrapped() {
        whenever(wrapped.hasObservers()).thenReturn(true)

        assertTrue(underTest.hasObservers())
        verify(wrapped).hasObservers()
    }

    @Test
    fun returnsHasThrowableFromWrapped() {
        whenever(wrapped.hasThrowable()).thenReturn(true)

        assertTrue(underTest.hasThrowable())
        verify(wrapped).hasThrowable()
    }

    @Test
    fun returnsThrowableFromWrapped() {
        val throwable = IOException()
        whenever(wrapped.throwable).thenReturn(throwable)

        assertEquals(throwable, underTest.throwable)
        verify(wrapped).throwable
    }

    @Test
    fun suppliesInitialValueOnSubscribeActualWhenNoInitialValue() {
        invokeSubscribeActual(underTest, mock())
        verify(wrapped).onNext(initialValueSupplier.invoke())
    }

    @Test
    fun doesNotSupplyInitialValueOnSubscribeActualWhenHasInitialValue() {
        whenever(wrapped.hasValue()).thenReturn(true)

        invokeSubscribeActual(underTest, mock())

        verify(wrapped, never()).onNext(any())
    }

    private fun <T> invokeSubscribeActual(
        receiver: AsyncInitialValueBehaviorSubject<T>,
        argument: Observer<T>
    ) {
        underTest.javaClass.getDeclaredMethod("subscribeActual", Observer::class.java).apply {
            isAccessible = true
            invoke(receiver, argument)
        }
    }
}
