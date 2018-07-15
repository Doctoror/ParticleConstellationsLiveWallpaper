package com.doctoror.particleswallpaper.data.config

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class NoPoolGlideModule : AppGlideModule() {

    override fun isManifestParsingEnabled() = false

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setBitmapPool(BitmapPoolAdapter())
    }
}
