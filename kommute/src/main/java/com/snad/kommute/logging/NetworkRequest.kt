package com.snad.kommute.logging

internal sealed class NetworkRequest {
    abstract val id: Long
    abstract val timestampMillis: Long
    abstract val url: String
    abstract val method: String
    abstract val requestBody: String?
    abstract val requestHeaders: Map<String, List<String>>

    internal data class Ongoing(
        override val id: Long,
        override val timestampMillis: Long,
        override val url: String,
        override val method: String,
        override val requestBody: String?,
        override val requestHeaders: Map<String, List<String>>,
    ): NetworkRequest()

    internal data class Finished(
        override val id: Long,
        override val timestampMillis: Long,
        override val url: String,
        override val method: String,
        override val requestBody: String?,
        override val requestHeaders: Map<String, List<String>>,

        val durationMillis: Long,
        val statusCode: Int,
        val responseBody: String?,
        val responseHeaders: Map<String, List<String>>,
    ): NetworkRequest()

    internal data class Failed(
        override val id: Long,
        override val timestampMillis: Long,
        override val url: String,
        override val method: String,
        override val requestBody: String?,
        override val requestHeaders: Map<String, List<String>>,

        val errorMessage: String
    ): NetworkRequest()
}