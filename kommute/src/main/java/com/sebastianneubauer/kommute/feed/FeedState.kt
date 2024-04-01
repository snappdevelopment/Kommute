package com.sebastianneubauer.kommute.feed

import androidx.compose.runtime.Immutable

@Immutable
internal sealed class FeedState {

    data object Loading : FeedState()

    data class Content(
        val requests: List<NetworkRequestListItem>
    ) : FeedState()

    data object Empty : FeedState()
}
