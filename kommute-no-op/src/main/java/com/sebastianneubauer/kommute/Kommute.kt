package com.sebastianneubauer.kommute

import android.content.Context
import com.sebastianneubauer.kommute.logging.NoopKommuteInterceptor
import okhttp3.Interceptor

public interface Kommute {
    public fun start(context: Context)

    public fun getInterceptor(): Interceptor

    public companion object {
        public fun getInstance(): Kommute {
            return NoopKommute()
        }
    }
}

private class NoopKommute: Kommute {

    override fun start(context: Context) {
        //noop
    }

    override fun getInterceptor(): Interceptor {
        return NoopKommuteInterceptor()
    }
}