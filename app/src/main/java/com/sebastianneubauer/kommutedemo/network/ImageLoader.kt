package com.sebastianneubauer.kommutedemo.network

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache

internal fun getImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .okHttpClient(OkHttpClientFactory.getOkHttpClientWithoutCache())
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .build()
        }
        .build()
}