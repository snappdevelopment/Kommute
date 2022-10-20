package com.sebastianneubauer.kommutedemo.network

import android.content.Context
import com.sebastianneubauer.kommute.Kommute
import java.io.File
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal fun getRetrofit(context: Context): Retrofit {

    val kommuteInterceptor = Kommute.getInstance().getInterceptor()

    val cache = Cache(
        directory = File(context.cacheDir, "http_cache"),
        maxSize = 50L * 1024L * 1024L // 50 MiB
    )

    val httpClient = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(kommuteInterceptor)
        .build()

    return Retrofit.Builder()
        .baseUrl("https://api.coindesk.com/")
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}