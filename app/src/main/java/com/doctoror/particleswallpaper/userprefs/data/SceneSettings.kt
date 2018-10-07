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
import io.reactivex.subjects.BehaviorSubject

const val NO_URI = ""

const val SCENE_PREFERENCES_NAME = "prefs"

private const val KEY_BACKGROUND_COLOR = "backgroundColor"

private const val KEY_BACKGROUND_URI = "backgroundUri"

private const val KEY_DENSITY = "numDots"

private const val KEY_FRAME_DELAY = "frameDelay"

private const val KEY_LINE_SCALE = "lineScale"

private const val KEY_LINE_LENGTH = "lineDistance"

private const val KEY_PARTICLE_COLOR = "particlesColor"

private const val KEY_PARTICLE_SCALE = "dotScale"

private const val KEY_SPEED_FACTOR = "stepMultiplier"

class SceneSettings(
    private val defaults: DefaultSceneSettings,
    private val prefs: SharedPreferences
) {
    private val backgroundColorSubject = BehaviorSubject.create<Int>().toSerialized()
    private val backgroundUriSubject = BehaviorSubject.create<String>().toSerialized()
    private val densitySubject = BehaviorSubject.create<Int>().toSerialized()
    private val frameDelaySubject = BehaviorSubject.create<Int>().toSerialized()
    private val lineLengthSubject = BehaviorSubject.create<Float>().toSerialized()
    private val lineScaleSubject = BehaviorSubject.create<Float>().toSerialized()
    private val particleColorSubject = BehaviorSubject.create<Int>().toSerialized()
    private val particleScaleSubject = BehaviorSubject.create<Float>().toSerialized()
    private val speedFactorSubject = BehaviorSubject.create<Float>().toSerialized()

    var backgroundColor
        get() = prefs.getInt(KEY_BACKGROUND_COLOR, defaults.backgroundColor)
        set(value) {
            prefs.edit().putInt(KEY_BACKGROUND_COLOR, value).apply()
            backgroundColorSubject.onNext(value)
        }

    var backgroundUri
        get() = prefs.getString(KEY_BACKGROUND_URI, defaults.backgroundUri)
        set(value) {
            prefs.edit().putString(KEY_BACKGROUND_URI, value).apply()
            backgroundUriSubject.onNext(value)
        }

    var density
        get() = prefs.getInt(KEY_DENSITY, defaults.density)
        set(value) {
            prefs.edit().putInt(KEY_DENSITY, value).apply()
            densitySubject.onNext(value)
        }

    var frameDelay
        get() = prefs.getInt(KEY_FRAME_DELAY, defaults.frameDelay)
        set(value) {
            prefs.edit().putInt(KEY_FRAME_DELAY, value).apply()
            frameDelaySubject.onNext(value)
        }

    var lineLength
        get() = prefs.getFloat(KEY_LINE_LENGTH, defaults.lineLength)
        set(value) {
            prefs.edit().putFloat(KEY_LINE_LENGTH, value).apply()
            lineLengthSubject.onNext(value)
        }

    var lineScale
        get() = prefs.getFloat(KEY_LINE_SCALE, defaults.lineScale)
        set(value) {
            prefs.edit().putFloat(KEY_LINE_SCALE, value).apply()
            lineScaleSubject.onNext(value)
        }

    var particleColor
        get() = prefs.getInt(KEY_PARTICLE_COLOR, defaults.particleColor)
        set(value) {
            prefs.edit().putInt(KEY_PARTICLE_COLOR, value).apply()
            particleColorSubject.onNext(value)
        }

    var particleScale
        get() = prefs.getFloat(KEY_PARTICLE_SCALE, defaults.particleScale)
        set(value) {
            prefs.edit().putFloat(KEY_PARTICLE_SCALE, value).apply()
            particleScaleSubject.onNext(value)
        }

    var speedFactor
        get() = prefs.getFloat(KEY_SPEED_FACTOR, defaults.speedFactor)
        set(value) {
            prefs.edit().putFloat(KEY_SPEED_FACTOR, value).apply()
            speedFactorSubject.onNext(value)
        }

    init {
        backgroundColorSubject.onNext(backgroundColor)
        backgroundUriSubject.onNext(backgroundUri)
        densitySubject.onNext(density)
        frameDelaySubject.onNext(frameDelay)
        lineLengthSubject.onNext(lineLength)
        lineScaleSubject.onNext(lineScale)
        particleColorSubject.onNext(particleColor)
        particleScaleSubject.onNext(particleScale)
        speedFactorSubject.onNext(speedFactor)
    }

    fun observeBackgroundColor(): Observable<Int> = backgroundColorSubject

    fun observeBackgroundUri(): Observable<String> = backgroundUriSubject

    fun observeDensity(): Observable<Int> = densitySubject

    fun observeFrameDelay(): Observable<Int> = frameDelaySubject

    fun observeLineLength(): Observable<Float> = lineLengthSubject

    fun observeLineScale(): Observable<Float> = lineScaleSubject

    fun observeParticleColor(): Observable<Int> = particleColorSubject

    fun observeParticleScale(): Observable<Float> = particleScaleSubject

    fun observeSpeedFactor(): Observable<Float> = speedFactorSubject
}
