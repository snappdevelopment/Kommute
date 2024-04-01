package com.sebastianneubauer.kommute.details

import androidx.compose.runtime.Immutable

@Immutable
internal sealed class DetailsState {

    data object Loading : DetailsState()

    data class Content(
        val networkRequestDetailsItem: NetworkRequestDetailsItem
    ) : DetailsState()

    data object Error : DetailsState()
}
