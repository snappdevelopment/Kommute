package com.sebastianneubauer.kommute

import android.content.Context
import com.sebastianneubauer.kommute.logging.NetworkDataRepository
import com.sebastianneubauer.kommute.logging.KommuteInterceptor
import okhttp3.Interceptor

public interface Kommute {
    /**
     * Starts Kommute by sending a system notification, which can be expanded into a chat bubble.
     * Network traffic will be displayed in this bubble.
     *
     * Make sure to grant the notification permission for your app in the system settings on
     * Android 13+.
     */
    public fun start(context: Context)

    /**
     * Returns an [okhttp3.Interceptor], which should be added to the OkHttp instance to
     * observe network traffic with Kommute.
     */
    public fun getInterceptor(): Interceptor

    /**
     * Returns the same instance of Kommute whenever [getInstance] is called.
     */
    public companion object {
        internal val repository = NetworkDataRepository.get()
        private val kommute by lazy { AndroidKommute(repository) }

        public fun getInstance(): Kommute {
            return kommute
        }
    }
}

private class AndroidKommute(
    private val repository: NetworkDataRepository
): Kommute {

    override fun start(context: Context) {
        KommuteNotification.send(context)
    }

    override fun getInterceptor(): Interceptor {
        return KommuteInterceptor(repository)
    }
}