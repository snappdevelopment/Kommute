package com.snad.kommute

import android.content.Context
import com.snad.kommute.logging.NoopKommuteInterceptor
import okhttp3.Interceptor

public interface Kommute {
    public fun getInterceptor(): Interceptor

    public fun start(context: Context)

    public object Factory {
        public fun get(): Kommute {
            return NoopKommute()
        }
    }
}

private class NoopKommute: Kommute {

    override fun getInterceptor(): Interceptor {
        return NoopKommuteInterceptor()
    }

    override fun start(context: Context) {
        //noop
    }
}