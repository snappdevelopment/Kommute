package com.sebastianneubauer.kommute.logging

import okhttp3.Interceptor
import okhttp3.Response

internal class NoopKommuteInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}