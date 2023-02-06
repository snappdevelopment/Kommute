package com.sebastianneubauer.kommute.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sebastianneubauer.kommute.logging.NetworkDataRepository
import com.sebastianneubauer.kommute.logging.NetworkRequest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.json.JSONObject

internal class DetailsViewModel(
    private val repository: NetworkDataRepository
) : ViewModel() {

    fun state(requestId: Long): StateFlow<DetailsState> = flowOf(
        repository.request(requestId).toDetailsState()
    )
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DetailsState.Initial)

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
        val formattedRequestBody = requestBody?.runCatching {
            JSONObject(this).toString(4)
        }?.getOrNull()

        val formattedRequestHeaders = requestHeaders.mapValues {
            it.value.reduce { acc, s -> "$acc, $s" }
        }

        return when (this) {
            is NetworkRequest.Finished -> {
                val formattedResponseBody = responseBody?.runCatching {
                    JSONObject(this).toString(4)
                }?.getOrNull()

                val formattedResponseHeaders = responseHeaders.mapValues {
                    it.value.reduce { acc, s -> "$acc, $s" }
                }

                NetworkRequestDetailsItem(
                    url = url,
                    requestBody = formattedRequestBody,
                    requestHeaders = formattedRequestHeaders,
                    responseBody = formattedResponseBody,
                    responseHeaders = formattedResponseHeaders
                )
            }
            is NetworkRequest.Ongoing -> NetworkRequestDetailsItem(
                url = url,
                requestBody = formattedRequestBody,
                requestHeaders = formattedRequestHeaders,
                responseBody = null,
                responseHeaders = null
            )
            is NetworkRequest.Failed -> NetworkRequestDetailsItem(
                url = url,
                requestBody = formattedRequestBody,
                requestHeaders = formattedRequestHeaders,
                responseBody = null,
                responseHeaders = null
            )
        }
    }

    class Factory(
        private val repository: NetworkDataRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("Unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailsViewModel(repository) as T
        }
    }
}
