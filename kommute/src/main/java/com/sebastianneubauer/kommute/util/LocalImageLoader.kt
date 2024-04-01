package com.sebastianneubauer.kommute.util

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

/**
 * An ImageLoader to display images and gifs in Kommute.
 * This ImageLoader instance is separate from the consumer app one, to avoid
 * logging calls to the same gif whenever it is shown in Kommute.
 */
internal fun localImageLoader(context: Context) = ImageLoader.Builder(context)
    .components {
        if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
    }
    .build()
