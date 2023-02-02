package com.sebastianneubauer.kommute.feed

internal sealed class FeedState

internal data class Content(
    val requests: List<NetworkRequestListItem>
) : FeedState()

internal object Loading : FeedState()

internal object Empty : FeedState()
