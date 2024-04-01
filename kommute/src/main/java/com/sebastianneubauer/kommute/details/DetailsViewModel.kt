package com.sebastianneubauer.kommute.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sebastianneubauer.kommute.logging.NetworkDataRepository
import com.sebastianneubauer.kommute.logging.NetworkRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

internal class DetailsViewModel(
    private val repository: NetworkDataRepository,
    private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val requestId: MutableStateFlow<Long> = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<DetailsState> = requestId
        .flatMapLatest { repository.request(it) }
        .mapLatest { it.toDetailsState() }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailsState.Loading
        )

    fun setRequestId(id: Long) {
        requestId.value = id
    }

    private fun NetworkRequest?.toDetailsState(): DetailsState {
        return if (this == null) {
            DetailsState.Error
        } else {
            DetailsState.Content(
                networkRequestDetailsItem = this.toNetworkRequestDetailsItem()
            )
        }
    }

    private fun NetworkRequest.toNetworkRequestDetailsItem(): NetworkRequestDetailsItem {
        val formattedRequestHeaders = requestHeaders.mapValues {
            it.value.reduce { acc, s -> "$acc, $s" }
        }

        return when (this) {
            is NetworkRequest.Finished -> {
                val formattedResponseHeaders = responseHeaders.mapValues {
                    it.value.reduce { acc, s -> "$acc, $s" }
                }

                NetworkRequestDetailsItem(
                    url = url,
                    requestBody = requestBody,
                    requestHeaders = formattedRequestHeaders,
                    responseBody = responseBody,
                    responseHeaders = formattedResponseHeaders
                )
            }
            is NetworkRequest.Ongoing -> NetworkRequestDetailsItem(
                url = url,
                requestBody = requestBody,
                requestHeaders = formattedRequestHeaders,
                responseBody = null,
                responseHeaders = null
            )
            is NetworkRequest.Failed -> NetworkRequestDetailsItem(
                url = url,
                requestBody = requestBody,
                requestHeaders = formattedRequestHeaders,
                responseBody = null,
                responseHeaders = null
            )
        }
    }

    class Factory(
        private val repository: NetworkDataRepository,
        private val defaultDispatcher: CoroutineDispatcher,
    ) : ViewModelProvider.Factory {
        @Suppress("Unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailsViewModel(
                repository = repository,
                defaultDispatcher = defaultDispatcher,
            ) as T
        }
    }
}
