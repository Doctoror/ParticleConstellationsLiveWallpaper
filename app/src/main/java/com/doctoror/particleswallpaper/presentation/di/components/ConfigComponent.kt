package com.doctoror.particleswallpaper.presentation.di.components

import android.content.Context
import com.doctoror.particleswallpaper.data.engine.WallpaperServiceImpl
import com.doctoror.particleswallpaper.domain.config.SceneConfigurator
import com.doctoror.particleswallpaper.domain.execution.SchedulersProvider
import com.doctoror.particleswallpaper.domain.file.BackgroundImageManager
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import com.doctoror.particleswallpaper.presentation.di.modules.AppModule
import com.doctoror.particleswallpaper.presentation.di.modules.ConfigModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 *
 * Component for configuration-related code
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, ConfigModule::class))
interface ConfigComponent {

    fun exposeDrawableConfigurator(): SceneConfigurator
    fun exposeBackgroundImageManager(): BackgroundImageManager
    fun exposeMutableSettings(): MutableSettingsRepository
    fun exposeSettings(): SettingsRepository

    fun exposeSchedulers(): SchedulersProvider
    fun exposeContext(): Context

    fun inject(p: WallpaperServiceImpl.EngineImpl)
}