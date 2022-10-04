package com.sebastianneubauer.kommute

import android.content.Context
import com.sebastianneubauer.kommute.logging.NetworkDataRepository
import com.sebastianneubauer.kommute.logging.KommuteInterceptor
import okhttp3.Interceptor

public interface Kommute {
    public fun getInterceptor(): Interceptor

    public fun start(context: Context)

    public object Factory {
        internal val repository = NetworkDataRepository.get()

        public fun get(): Kommute {
            return AndroidKommute(repository)
        }
    }
}

private class AndroidKommute(
    private val repository: NetworkDataRepository
): Kommute {

    override fun getInterceptor(): Interceptor {
        return KommuteInterceptor(repository)
    }

    override fun start(context: Context) {
        KommuteNotification.send(context)
    }
}