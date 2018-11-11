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
package com.doctoror.particleswallpaper.engine.configurator

/**
 * Maps particle scale to particle radius range
 */
object ParticleRadiusMapper {

    private const val MIN_PARTICLE_RADIUS = 1f
    private const val MAX_PARTICLE_RADIUS = 3f

    fun transform(scale: Float) = MIN_PARTICLE_RADIUS * scale to MAX_PARTICLE_RADIUS * scale
}
