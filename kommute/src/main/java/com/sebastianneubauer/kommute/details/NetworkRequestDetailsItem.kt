package com.sebastianneubauer.kommute.details

internal data class NetworkRequestDetailsItem(
    val url: String,
    val requestBody: String?,
    val requestHeaders: Map<String, String>,
    val responseBody: String?,
    val responseHeaders: Map<String, String>?,
) {
    val isImage: Boolean
        get() {
            val urlIsImage = imageTypes.any { url.endsWith(it, true) }
            val contentTypeIsImage = imageContentTypes.any { responseHeaders?.get("content-type") == it }

            return contentTypeIsImage || urlIsImage
        }
}

//image types supported by coil
private val imageTypes = listOf(".jpg", ".jpeg", ".png", ".bmp", ".webp", ".heif")
private val imageContentTypes = listOf("image/jpg", "image/jpeg", "image/png", "image/bmp", "image/webp", "image/heif")