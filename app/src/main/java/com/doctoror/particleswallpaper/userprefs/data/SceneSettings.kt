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

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.subjects.AsyncInitialValueBehaviorSubject

const val NO_URI = ""

const val PREFERENCES_NAME_SCENE = "prefs"

private const val KEY_BACKGROUND_COLOR = "backgroundColor"

private const val KEY_BACKGROUND_SCROLL = "backgroundScroll"

private const val KEY_BACKGROUND_URI = "backgroundUri"

private const val KEY_DENSITY = "numDots"

private const val KEY_FRAME_DELAY = "frameDelay"

private const val KEY_LINE_SCALE = "lineScale"

private const val KEY_LINE_LENGTH = "lineDistance"

private const val KEY_PARTICLE_COLOR = "particlesColor"

private const val KEY_PARTICLE_SCALE = "dotScale"

private const val KEY_PARTICLES_SCROLL = "particlesScroll"

private const val KEY_SPEED_FACTOR = "stepMultiplier"

class SceneSettings(
    private val defaults: DefaultSceneSettings,
    private val prefsSource: () -> SharedPreferences
) {
    private val backgroundColorSubject =
        AsyncInitialValueBehaviorSubject { backgroundColor }.toSerialized()

    private val backgroundScrollSubject =
        AsyncInitialValueBehaviorSubject { backgroundScroll }.toSerialized()

    private val backgroundUriSubject =
        AsyncInitialValueBehaviorSubject { backgroundUri }.toSerialized()

    private val densitySubject =
        AsyncInitialValueBehaviorSubject { density }.toSerialized()

    private val frameDelaySubject =
        AsyncInitialValueBehaviorSubject { frameDelay }.toSerialized()

    private val lineLengthSubject =
        AsyncInitialValueBehaviorSubject { lineLength }.toSerialized()

    private val lineScaleSubject =
        AsyncInitialValueBehaviorSubject { lineScale }.toSerialized()

    private val particleColorSubject =
        AsyncInitialValueBehaviorSubject { particleColor }.toSerialized()

    private val particleScaleSubject =
        AsyncInitialValueBehaviorSubject { particleScale }.toSerialized()

    private val particlesScrollSubject =
        AsyncInitialValueBehaviorSubject { particlesScroll }.toSerialized()

    private val speedFactorSubject =
        AsyncInitialValueBehaviorSubject { speedFactor }.toSerialized()

    var backgroundColor
        get() = prefsSource().getInt(KEY_BACKGROUND_COLOR, defaults.backgroundColor)
        set(value) {
            prefsSource().edit().putInt(KEY_BACKGROUND_COLOR, value).apply()
            backgroundColorSubject.onNext(value)
        }

    var backgroundScroll
        get() = prefsSource().getBoolean(KEY_BACKGROUND_SCROLL, defaults.backgroundScroll)
        set(value) {
            prefsSource().edit().putBoolean(KEY_BACKGROUND_SCROLL, value).apply()
            backgroundScrollSubject.onNext(value)
        }

    var backgroundUri
        get() = prefsSource().getString(KEY_BACKGROUND_URI, defaults.backgroundUri)!!
        set(value) {
            prefsSource().edit().putString(KEY_BACKGROUND_URI, value).apply()
            backgroundUriSubject.onNext(value)
        }

    var density
        get() = prefsSource().getInt(KEY_DENSITY, defaults.density)
        set(value) {
            prefsSource().edit().putInt(KEY_DENSITY, value).apply()
            densitySubject.onNext(value)
        }

    var frameDelay
        get() = prefsSource().getInt(KEY_FRAME_DELAY, defaults.frameDelay)
        set(value) {
            prefsSource().edit().putInt(KEY_FRAME_DELAY, value).apply()
            frameDelaySubject.onNext(value)
        }

    var lineLength
        get() = prefsSource().getFloat(KEY_LINE_LENGTH, defaults.lineLength)
        set(value) {
            prefsSource().edit().putFloat(KEY_LINE_LENGTH, value).apply()
            lineLengthSubject.onNext(value)
        }

    var lineScale
        get() = prefsSource().getFloat(KEY_LINE_SCALE, defaults.lineScale)
        set(value) {
            prefsSource().edit().putFloat(KEY_LINE_SCALE, value).apply()
            lineScaleSubject.onNext(value)
        }

    var particleColor
        get() = prefsSource().getInt(KEY_PARTICLE_COLOR, defaults.particleColor)
        set(value) {
            prefsSource().edit().putInt(KEY_PARTICLE_COLOR, value).apply()
            particleColorSubject.onNext(value)
        }

    var particleScale
        get() = prefsSource().getFloat(KEY_PARTICLE_SCALE, defaults.particleScale)
        set(value) {
            prefsSource().edit().putFloat(KEY_PARTICLE_SCALE, value).apply()
            particleScaleSubject.onNext(value)
        }

    var particlesScroll
        get() = prefsSource().getBoolean(KEY_PARTICLES_SCROLL, defaults.particlesScroll)
        set(value) {
            prefsSource().edit().putBoolean(KEY_PARTICLES_SCROLL, value).apply()
            particlesScrollSubject.onNext(value)
        }

    var speedFactor
        get() = prefsSource().getFloat(KEY_SPEED_FACTOR, defaults.speedFactor)
        set(value) {
            prefsSource().edit().putFloat(KEY_SPEED_FACTOR, value).apply()
            speedFactorSubject.onNext(value)
        }

    fun observeBackgroundColor(): Observable<Int> = backgroundColorSubject

    fun observeBackgroundScroll(): Observable<Boolean> = backgroundScrollSubject

    fun observeBackgroundUri(): Observable<String> = backgroundUriSubject

    fun observeDensity(): Observable<Int> = densitySubject

    fun observeFrameDelay(): Observable<Int> = frameDelaySubject

    fun observeLineLength(): Observable<Float> = lineLengthSubject

    fun observeLineScale(): Observable<Float> = lineScaleSubject

    fun observeParticleColor(): Observable<Int> = particleColorSubject

    fun observeParticleScale(): Observable<Float> = particleScaleSubject

    fun observeParticlesScroll(): Observable<Boolean> = particlesScrollSubject

    fun observeSpeedFactor(): Observable<Float> = speedFactorSubject
}
