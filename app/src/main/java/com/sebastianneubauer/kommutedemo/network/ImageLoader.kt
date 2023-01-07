package com.sebastianneubauer.kommutedemo.network

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache

internal fun getImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .okHttpClient(OkHttpClientFactory.getOkHttpClientWithoutCache())
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .build()
        }
        .build()
}