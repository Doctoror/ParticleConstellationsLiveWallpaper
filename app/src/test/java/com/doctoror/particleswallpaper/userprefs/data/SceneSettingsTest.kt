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
package com.doctoror.particleswallpaper.userprefs.data

import android.annotation.TargetApi
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty0

@TargetApi(Build.VERSION_CODES.M)
class SceneSettingsTest {

    private lateinit var settings: SceneSettings

    @BeforeEach
    fun setUp() {
        val fakePrefs = InMemorySharedPreferences()

        val resources = mock<Resources>(Resources::class.java)
        val theme = mock(Resources.Theme::class.java)
        val typedValue = mock(TypedValue::class.java)
        val typedValueFactory = mock(DefaultSceneSettings.TypedValueFactory::class.java)

        whenever(resources.getInteger(anyInt())).thenReturn(1)
        whenever(resources.getDimension(anyInt())).thenReturn(1f)
        whenever(resources.getColor(anyInt(), ArgumentMatchers.eq(theme))).thenReturn(1)
        whenever(resources.getColor(anyInt(), ArgumentMatchers.eq(theme))).thenReturn(1)

        whenever(typedValue.float).thenReturn(1f)
        whenever(typedValueFactory.newTypedValue()).thenReturn(typedValue)

        settings = SceneSettings(
            DefaultSceneSettings(resources, theme, typedValueFactory)
        ) { fakePrefs }
    }

    private fun <T> assertObserverHasValueCount(o: TestObserver<T>, count: Int) {
        o.assertNoErrors()
        o.assertNotComplete()
        o.assertSubscribed()
        o.assertValueCount(count)
    }

    private fun <T> assertObservableHasValueCount(
        o: Observable<T>,
        valueCount: Int
    ): TestObserver<T> {
        val observer = TestObserver.create<T>()
        o.subscribe(observer)
        assertObserverHasValueCount(observer, valueCount)

        return observer
    }

    private fun <T> assertObservableHasValue(o: Observable<T>, value: T) {
        val testObserver = assertObservableHasValueCount(o, 1)
        testObserver.assertValue(value)
    }

    private fun <T> testPreferenceObservable(accessor: KFunction<Observable<T>>) {
        assertObservableHasValueCount(accessor.call(), 1)
    }

    private fun <T> testPreferenceMutator(
        observableAccessor: KFunction<Observable<T>>,
        property: KMutableProperty0<T>,
        testValue: T
    ) {
        property.set(testValue)
        assertObservableHasValue(observableAccessor.call(), testValue)
    }

    private fun <T> testPreferenceNotifiesChanges(
        accessor: KFunction<Observable<T>>,
        property: KMutableProperty0<T>,
        testValue: T
    ) {
        val observer = TestObserver.create<T>()
        accessor.call().subscribe(observer)

        assertObserverHasValueCount(observer, 1)

        property.set(testValue)

        assertObserverHasValueCount(observer, 2)
        observer.assertValueAt(1) { v -> v == testValue }
    }

    @Test
    fun testBackgroundColorObservable() {
        testPreferenceObservable(settings::observeBackgroundColor)
    }

    @Test
    fun testBackgroundColorMutator() {
        testPreferenceMutator(
            settings::observeBackgroundColor,
            settings::backgroundColor,
            0xff000002.toInt()
        )
    }

    @Test
    fun testBackgroundColorMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeBackgroundColor,
            settings::backgroundColor,
            0xff000003.toInt()
        )
    }

    @Test
    fun testBackgroundScrollObservable() {
        testPreferenceObservable(settings::observeBackgroundScroll)
    }

    @Test
    fun testBackgroundScrollMutator() {
        testPreferenceMutator(
            settings::observeBackgroundScroll,
            settings::backgroundScroll,
            true
        )
    }

    @Test
    fun testBackgroundScrollMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeBackgroundScroll,
            settings::backgroundScroll,
            true
        )
    }

    @Test
    fun testBackgroundUriObservable() {
        testPreferenceObservable(settings::observeBackgroundUri)
    }

    @Test
    fun testBackgroundUriMutator() {
        testPreferenceMutator(
            settings::observeBackgroundUri,
            settings::backgroundUri,
            "uri://a"
        )
    }

    @Test
    fun testBackgroundUriMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeBackgroundUri,
            settings::backgroundUri,
            "uri://b"
        )
    }

    @Test
    fun testDensityObservable() {
        testPreferenceObservable(settings::observeDensity)
    }

    @Test
    fun testDensityMutator() {
        testPreferenceMutator(
            settings::observeDensity,
            settings::density,
            1
        )
    }

    @Test
    fun testDensityMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeDensity,
            settings::density,
            2
        )
    }

    @Test
    fun testFrameDelayObservable() {
        testPreferenceObservable(settings::observeFrameDelay)
    }

    @Test
    fun testFrameDelayMutator() {
        testPreferenceMutator(
            settings::observeFrameDelay,
            settings::frameDelay,
            3
        )
    }

    @Test
    fun testFrameDelayMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeFrameDelay,
            settings::frameDelay,
            4
        )
    }

    @Test
    fun testLineLengthObservable() {
        testPreferenceObservable(settings::observeLineLength)
    }

    @Test
    fun testLineLengthMutator() {
        testPreferenceMutator(
            settings::observeLineLength,
            settings::lineLength,
            0.7f
        )
    }

    @Test
    fun testLineLengthMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeLineLength,
            settings::lineLength,
            0.8f
        )
    }

    @Test
    fun testLineScaleObservable() {
        testPreferenceObservable(settings::observeLineScale)
    }

    @Test
    fun testLineScaleMutator() {
        testPreferenceMutator(
            settings::observeLineScale,
            settings::lineScale,
            0.5f
        )
    }

    @Test
    fun testLineScaleMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeLineScale,
            settings::lineScale,
            0.6f
        )
    }

    @Test
    fun testParticleColorObservable() {
        testPreferenceObservable(settings::observeParticleColor)
    }

    @Test
    fun testParticleColorMutator() {
        testPreferenceMutator(
            settings::observeParticleColor,
            settings::particleColor,
            0xff000000.toInt()
        )
    }

    @Test
    fun testParticleColorMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeParticleColor,
            settings::particleColor,
            0xff000001.toInt()
        )
    }

    @Test
    fun testParticleScaleObservable() {
        testPreferenceObservable(settings::observeParticleScale)
    }

    @Test
    fun testParticleScaleMutator() {
        testPreferenceMutator(
            settings::observeParticleScale,
            settings::particleScale,
            0.3f
        )
    }

    @Test
    fun testParticleScaleMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeParticleScale,
            settings::particleScale,
            0.4f
        )
    }

    @Test
    fun testSpeedFactorObservable() {
        testPreferenceObservable(settings::observeSpeedFactor)
    }

    @Test
    fun testSpeedFactorMutator() {
        testPreferenceMutator(
            settings::observeSpeedFactor,
            settings::speedFactor,
            0.1f
        )
    }

    @Test
    fun testSpeedFactorMutatorNotifiesChanges() {
        testPreferenceNotifiesChanges(
            settings::observeSpeedFactor,
            settings::speedFactor,
            0.2f
        )
    }
}
