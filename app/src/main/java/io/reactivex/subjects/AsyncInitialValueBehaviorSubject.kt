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

import androidx.annotation.VisibleForTesting
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * [BehaviorSubject] wrapper which supplies initial value from [initialValueSupplier] on
 * [subscribeActual].
 */
class AsyncInitialValueBehaviorSubject<T : Any> : Subject<T> {

    private val initialValueSupplier: () -> T

    private val wrapped: BehaviorSubject<T>

    constructor(initialValueSupplier: () -> T) : this(
        initialValueSupplier,
        BehaviorSubject.create()
    )

    @VisibleForTesting
    constructor(
        initialValueSupplier: () -> T,
        wrapped: BehaviorSubject<T>
    ) {
        this.initialValueSupplier = initialValueSupplier
        this.wrapped = wrapped
    }

    override fun getThrowable() = wrapped.throwable

    override fun hasComplete() = wrapped.hasComplete()

    override fun hasObservers() = wrapped.hasObservers()

    override fun hasThrowable() = wrapped.hasThrowable()

    override fun onComplete() = wrapped.onComplete()

    override fun onError(e: Throwable) = wrapped.onError(e)

    override fun onNext(t: T) = wrapped.onNext(t)

    override fun onSubscribe(d: Disposable) = wrapped.onSubscribe(d)

    override fun subscribeActual(observer: Observer<in T>?) {
        wrapped.subscribeActual(observer)
        if (!wrapped.hasValue()) {
            wrapped.onNext(initialValueSupplier())
        }
    }

}
