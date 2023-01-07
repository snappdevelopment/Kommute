package com.sebastianneubauer.kommutedemo.network

import android.content.Context
import com.sebastianneubauer.kommute.Kommute
import java.io.File
import okhttp3.Cache
import okhttp3.OkHttpClient

internal object OkHttpClientFactory {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(Kommute.getInstance().getInterceptor())
        .build()

    fun getOkHttpClient(context: Context): OkHttpClient {
        val cache = Cache(
            directory = File(context.cacheDir, "http_cache"),
            maxSize = 50L * 1024L * 1024L // 50 MiB
        )

        return okHttpClient.newBuilder().cache(cache).build()
    }

    fun getOkHttpClientWithoutCache(): OkHttpClient {
        return okHttpClient
    }
}