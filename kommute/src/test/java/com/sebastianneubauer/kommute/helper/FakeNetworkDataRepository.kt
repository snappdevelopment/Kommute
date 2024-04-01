package com.sebastianneubauer.kommute.helper

import com.sebastianneubauer.kommute.logging.NetworkDataRepository
import com.sebastianneubauer.kommute.logging.NetworkRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf

internal class FakeNetworkDataRepository : NetworkDataRepository {

    var currentRequest: NetworkRequest? = null

    private val requestFlow = MutableSharedFlow<List<NetworkRequest>>(replay = 1)

    override val requests: Flow<List<NetworkRequest>> = requestFlow

    fun setRequests(requests: List<NetworkRequest>) {
        requestFlow.tryEmit(requests)
    }

    override fun request(id: Long): Flow<NetworkRequest?> {
        return flowOf(currentRequest)
    }

    override fun add(data: NetworkRequest) {
        // noop
    }

    override fun update(data: NetworkRequest) {
        // noop
    }

    override fun clear() {
        requestFlow.tryEmit(emptyList())
    }
}
