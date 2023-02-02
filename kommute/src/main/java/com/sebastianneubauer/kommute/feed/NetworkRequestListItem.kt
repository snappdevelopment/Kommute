package com.sebastianneubauer.kommute.feed

internal sealed class NetworkRequestListItem {
    abstract val id: Long
    abstract val dateTime: String
    abstract val url: String
    abstract val method: String

    internal data class Ongoing(
        override val id: Long,
        override val dateTime: String,
        override val url: String,
        override val method: String,
    ) : NetworkRequestListItem()

    internal data class Finished(
        override val id: Long,
        override val dateTime: String,
        override val url: String,
        override val method: String,

        val duration: String,
        val statusCode: Int,
    ) : NetworkRequestListItem()

    internal data class Failed(
        override val id: Long,
        override val dateTime: String,
        override val url: String,
        override val method: String,

        val errorMessage: String
    ) : NetworkRequestListItem()
}
