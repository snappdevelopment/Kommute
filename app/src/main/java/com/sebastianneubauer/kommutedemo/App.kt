package com.sebastianneubauer.kommutedemo

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.sebastianneubauer.kommutedemo.network.getImageLoader

class App: Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return getImageLoader(this)
    }
}