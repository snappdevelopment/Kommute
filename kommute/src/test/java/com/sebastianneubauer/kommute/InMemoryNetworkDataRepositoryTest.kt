package com.sebastianneubauer.kommute

import app.cash.turbine.test
import com.sebastianneubauer.kommute.logging.InMemoryNetworkDataRepository
import com.sebastianneubauer.kommute.logging.NetworkRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class InMemoryNetworkDataRepositoryTest {

    private val underTest = InMemoryNetworkDataRepository()

    private val ongoingRequest1 = NetworkRequest.Ongoing(
        id = 1,
        timestampMillis = 0,
        url = "",
        method = "",
        requestBody = null,
        requestHeaders = mapOf()
    )

    private val finishedRequest1 = NetworkRequest.Finished(
        id = 1,
        timestampMillis = 0,
        url = "",
        method = "",
        requestBody = null,
        requestHeaders = mapOf(),
        durationMillis = 0,
        statusCode = 0,
        responseBody = null,
        responseHeaders = mapOf()
    )

    private val finishedRequest2 = finishedRequest1.copy(id = 2)

    @Test
    fun `initial state emits empty list`() = runTest {
        underTest.requests.test {
            assertEquals(emptyList<NetworkRequest>(), awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `adding requests emits updated lists`() = runTest {
        underTest.requests.test {
            assertEquals(emptyList<NetworkRequest>(), awaitItem())

            underTest.add(finishedRequest1)

            assertEquals(listOf(finishedRequest1), awaitItem())

            underTest.add(finishedRequest2)

            assertEquals(listOf(finishedRequest1, finishedRequest2), awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `updating a request emits updated list`() = runTest {
        underTest.requests.test {
            assertEquals(emptyList<NetworkRequest>(), awaitItem())

            underTest.add(ongoingRequest1)

            assertEquals(listOf(ongoingRequest1), awaitItem())

            // updates ongoing request
            underTest.update(finishedRequest1)

            assertEquals(listOf(finishedRequest1), awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `clearing all requests emits empty list`() = runTest {
        underTest.requests.test {
            assertEquals(emptyList<NetworkRequest>(), awaitItem())

            underTest.add(finishedRequest1)

            assertEquals(listOf(finishedRequest1), awaitItem())

            underTest.add(finishedRequest2)

            assertEquals(listOf(finishedRequest1, finishedRequest2), awaitItem())

            underTest.clear()

            assertEquals(emptyList<NetworkRequest>(), awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `request() with valid id returns request`() = runTest {
        underTest.add(finishedRequest1)
        underTest.add(finishedRequest2)

        val item = underTest.request(id = 1)

        assertEquals(finishedRequest1, item)
    }

    @Test
    fun `request() with invalid id returns null`() = runTest {
        underTest.add(finishedRequest1)

        val item = underTest.request(id = 2)

        assertEquals(null, item)
    }
}
