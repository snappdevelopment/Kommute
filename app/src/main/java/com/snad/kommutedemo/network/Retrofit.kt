package com.snad.kommutedemo.network

import com.snad.kommute.Kommute
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal fun getRetrofit(): Retrofit {

    val kommuteInterceptor = Kommute.Factory.get().getInterceptor()

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(kommuteInterceptor)
        .build()

    return Retrofit.Builder()
        .baseUrl("https://api.coindesk.com/")
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}