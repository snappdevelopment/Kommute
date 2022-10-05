package com.sebastianneubauer.kommute.logging

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.util.Random
import java.util.concurrent.TimeUnit

internal class KommuteInterceptor(
    private val networkDataRepository: NetworkDataRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val timeStamp = System.currentTimeMillis()
        val url = request.url.toString()
        val method = request.method
        val requestHeaders = request.headers.toMultimap()
        val requestBodyString = request.toRequestBodyString()

        val id = Random().nextLong()
        val networkRequest = NetworkRequest.Ongoing(
            id = id,
            timestampMillis = timeStamp,
            url = url,
            method = method,
            requestBody = requestBodyString,
            requestHeaders = requestHeaders,
        )
        networkDataRepository.add(networkRequest)

        val startTime = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            val failedNetworkRequest = NetworkRequest.Failed(
                id = id,
                timestampMillis = timeStamp,
                url = url,
                method = method,
                requestBody = requestBodyString,
                requestHeaders = requestHeaders,
                errorMessage = e.toString()
            )
            networkDataRepository.update(failedNetworkRequest)
            throw e
        }

        val durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
        val statusCode = response.code
        val responseHeaders = response.headers.toMultimap()
        val responseBodyString: String? = response.toResponseBodyString()

        val finishedNetworkRequest = NetworkRequest.Finished(
            id = id,
            timestampMillis = timeStamp,
            url = url,
            method = method,
            requestBody = requestBodyString,
            requestHeaders = requestHeaders,
            statusCode = statusCode,
            durationMillis = durationMillis,
            responseBody = responseBodyString,
            responseHeaders = responseHeaders
        )

        networkDataRepository.update(finishedNetworkRequest)

        return response
    }

    private fun Request.toRequestBodyString(): String? {
        val body = this.body ?: return null

        var buffer = Buffer()
        runCatching { body.writeTo(buffer) }

        if (headers["Content-Encoding"].equals("gzip", ignoreCase = true)) {
            GzipSource(buffer).use { gzippedResponseBody ->
                buffer = Buffer()
                buffer.writeAll(gzippedResponseBody)
            }
        }

        val result = runCatching {
            val charset = body.contentType()?.charset() ?: Charsets.UTF_8
            buffer.readString(charset)
        }

        return result.getOrNull()
    }

    private fun Response.toResponseBodyString(): String? {
        val body = this.body ?: return null

        var buffer = body.source().apply { request(Long.MAX_VALUE) }.buffer

        if (headers["Content-Encoding"].equals("gzip", ignoreCase = true)) {
            GzipSource(buffer.clone()).use { gzippedResponseBody ->
                buffer = Buffer()
                buffer.writeAll(gzippedResponseBody)
            }
        }

        val result = runCatching {
            val charset = body.contentType()?.charset() ?: Charsets.UTF_8
            buffer.clone().readString(charset)
        }

        return result.getOrNull()
    }
}
