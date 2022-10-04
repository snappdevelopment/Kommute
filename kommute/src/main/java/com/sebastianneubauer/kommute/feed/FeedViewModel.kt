package com.sebastianneubauer.kommute.feed

import androidx.lifecycle.*
import com.sebastianneubauer.kommute.logging.NetworkDataRepository
import com.sebastianneubauer.kommute.logging.NetworkRequest
import com.sebastianneubauer.kommute.util.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class FeedViewModel(
    private val repository: NetworkDataRepository,
    private val dateTimeFormatter: DateTimeFormatter
): ViewModel() {

    val state: StateFlow<FeedState> = repository.requests
        .map { it.toFeedState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Loading
        )

    private fun List<NetworkRequest>.toFeedState(): FeedState {
        val items = this
            .reversed()
            .map {
                when(it) {
                    is NetworkRequest.Ongoing -> NetworkRequestListItem.Ongoing(
                        id = it.id,
                        dateTime = dateTimeFormatter.format(it.timestampMillis),
                        url = it.url,
                        method = it.method
                    )
                    is NetworkRequest.Finished -> NetworkRequestListItem.Finished(
                        id = it.id,
                        dateTime = dateTimeFormatter.format(it.timestampMillis),
                        url = it.url,
                        method = it.method,
                        duration = "${it.durationMillis}ms",
                        statusCode = it.statusCode
                    )
                    is NetworkRequest.Failed -> NetworkRequestListItem.Failed(
                        id = it.id,
                        dateTime = dateTimeFormatter.format(it.timestampMillis),
                        url = it.url,
                        method = it.method,
                        errorMessage = it.errorMessage
                    )
                }
            }

        return if(items.isEmpty()) Empty else Content(items)
    }

    fun clearClicked() {
        repository.clear()
    }

    class Factory(
        private val repository: NetworkDataRepository,
        private val dateTimeFormatter: DateTimeFormatter
    ): ViewModelProvider.Factory {
        @Suppress("Unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedViewModel(repository, dateTimeFormatter) as T
        }
    }
}