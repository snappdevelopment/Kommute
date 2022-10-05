package com.sebastianneubauer.kommute.details

internal data class NetworkRequestDetailsItem(
    val url: String,
    val requestBody: String?,
    val requestHeaders: Map<String, String>,
    val responseBody: String?,
    val responseHeaders: Map<String, String>?,
)