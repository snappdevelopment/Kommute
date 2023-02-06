package com.sebastianneubauer.kommute.details

internal sealed class DetailsState {

    object Initial : DetailsState()

    data class Content(
        val networkRequestDetailsItem: NetworkRequestDetailsItem
    ) : DetailsState()

    object Error : DetailsState()
}
