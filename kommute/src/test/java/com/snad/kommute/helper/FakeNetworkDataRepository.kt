package com.snad.kommute.helper

import com.snad.kommute.logging.NetworkDataRepository
import com.snad.kommute.logging.NetworkRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class FakeNetworkDataRepository: NetworkDataRepository {

    var currentRequest: NetworkRequest? = null

    private val requestFlow = MutableSharedFlow<List<NetworkRequest>>(replay = 1)

    override val requests: Flow<List<NetworkRequest>> =  requestFlow

    fun setRequests(requests: List<NetworkRequest>) {
        requestFlow.tryEmit(requests)
    }

    override fun request(id: Long): NetworkRequest? {
        return currentRequest
    }

    override fun add(data: NetworkRequest) {
       //noop
    }

    override fun update(data: NetworkRequest) {
        //noop
    }

    override fun clear() {
        requestFlow.tryEmit(emptyList())
    }
}