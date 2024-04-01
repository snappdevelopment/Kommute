package com.sebastianneubauer.kommute.logging

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest

internal interface NetworkDataRepository {
    /**
     * A flow which emits updates of the list of [NetworkRequest].
     */
    val requests: Flow<List<NetworkRequest>>

    /**
     * Returns the [NetworkRequest] with the given [id] or
     * null if the request doesn't exist.
     */
    fun request(id: Long): Flow<NetworkRequest?>

    /**
     * Adds a new [NetworkRequest] to the list of requests.
     */
    fun add(data: NetworkRequest)

    /**
     * Searches and replaces an already existing [NetworkRequest].
     * Fails silently, if the request couldn't be found in the list.
     */
    fun update(data: NetworkRequest)

    /**
     * Deletes all [NetworkRequest] from the list.
     */
    fun clear()

    companion object {
        fun get(): NetworkDataRepository {
            return InMemoryNetworkDataRepository()
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@VisibleForTesting
internal class InMemoryNetworkDataRepository : NetworkDataRepository {

    private val lock = Any()

    private val networkRequests = mutableListOf<NetworkRequest>()
    private val updates = MutableSharedFlow<List<NetworkRequest>>(replay = 1)

    override val requests: Flow<List<NetworkRequest>> = updates.onStart { emit(networkRequests) }

    override fun request(id: Long): Flow<NetworkRequest?> {
        return updates
            .transformLatest<List<NetworkRequest>, NetworkRequest?> { it.firstOrNull { it.id == id } }
            .onStart { emit(networkRequests.firstOrNull { it.id == id }) }
    }

    override fun add(data: NetworkRequest) {
        synchronized(lock) {
            networkRequests.add(data)
            updates.tryEmit(networkRequests.toList())
        }
    }

    override fun update(data: NetworkRequest) {
        synchronized(lock) {
            val index = networkRequests.indexOfFirst { it.id == data.id }
            if (index != -1) {
                networkRequests[index] = data
                updates.tryEmit(networkRequests.toList())
            }
        }
    }

    override fun clear() {
        synchronized(lock) {
            networkRequests.clear()
            updates.tryEmit(networkRequests.toList())
        }
    }
}
