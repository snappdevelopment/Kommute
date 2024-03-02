package com.sebastianneubauer.kommutedemo.network

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import com.sebastianneubauer.kommute.Kommute
import java.io.File
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal fun getRetrofit(context: Context): Retrofit {
    val httpClient = OkHttpClientFactory.getOkHttpClient(context)

    return Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}