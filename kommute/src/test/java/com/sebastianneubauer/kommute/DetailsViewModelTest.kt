package com.sebastianneubauer.kommute

import app.cash.turbine.test
import com.sebastianneubauer.kommute.details.DetailsState
import com.sebastianneubauer.kommute.details.DetailsViewModel
import com.sebastianneubauer.kommute.details.NetworkRequestDetailsItem
import com.sebastianneubauer.kommute.helper.FakeNetworkDataRepository
import com.sebastianneubauer.kommute.helper.MainDispatcherRule
import com.sebastianneubauer.kommute.logging.NetworkRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DetailsViewModelTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeNetworkDataRepository()

    private val underTest by lazy {
        DetailsViewModel(
            repository = repository,
            defaultDispatcher = UnconfinedTestDispatcher()
        )
    }

    private val finishedRequest = NetworkRequest.Finished(
        id = 0,
        timestampMillis = 0,
        url = "",
        method = "",
        requestBody = "{\"key\":\"value\"}",
        requestHeaders = mapOf("key" to listOf("value1", "value2")),
        durationMillis = 0,
        statusCode = 0,
        responseBody = "{\"key\":\"value\"}",
        responseHeaders = mapOf("key" to listOf("value1", "value2"))
    )

    @Test
    fun `finished request loaded successfully`() = runTest {
        repository.currentRequest = finishedRequest
        underTest.setRequestId(finishedRequest.id)

        underTest.state.test {
            assertEquals(DetailsState.Loading, awaitItem())

            val contentState = DetailsState.Content(
                networkRequestDetailsItem = NetworkRequestDetailsItem(
                    url = "",
                    requestBody = "{\"key\":\"value\"}",
                    requestHeaders = mapOf("key" to "value1, value2"),
                    responseBody = "{\"key\":\"value\"}",
                    responseHeaders = mapOf("key" to "value1, value2")
                )
            )

            assertEquals(contentState, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `ongoing request loaded successfully`() = runTest {
        val ongoingRequest = NetworkRequest.Ongoing(
            id = 0,
            timestampMillis = 0,
            url = "",
            method = "",
            requestBody = "{\"key\":\"value\"}",
            requestHeaders = mapOf("key" to listOf("value1", "value2"))
        )

        repository.currentRequest = ongoingRequest
        underTest.setRequestId(ongoingRequest.id)

        underTest.state.test {
            assertEquals(DetailsState.Loading, awaitItem())

            val contentState = DetailsState.Content(
                networkRequestDetailsItem = NetworkRequestDetailsItem(
                    url = "",
                    requestBody = "{\"key\":\"value\"}",
                    requestHeaders = mapOf("key" to "value1, value2"),
                    responseBody = null,
                    responseHeaders = null
                )
            )

            assertEquals(contentState, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `failed request loaded successfully`() = runTest {
        val failedRequest = NetworkRequest.Failed(
            id = 0,
            timestampMillis = 0,
            url = "",
            method = "",
            requestBody = "{\"key\":\"value\"}",
            requestHeaders = mapOf("key" to listOf("value1", "value2")),
            errorMessage = ""
        )

        repository.currentRequest = failedRequest
        underTest.setRequestId(failedRequest.id)

        underTest.state.test {
            assertEquals(DetailsState.Loading, awaitItem())

            val contentState = DetailsState.Content(
                networkRequestDetailsItem = NetworkRequestDetailsItem(
                    url = "",
                    requestBody = "{\"key\":\"value\"}",
                    requestHeaders = mapOf("key" to "value1, value2"),
                    responseBody = null,
                    responseHeaders = null
                )
            )

            assertEquals(contentState, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `request loading failed`() = runTest {
        underTest.setRequestId(99)

        underTest.state.test {
            assertEquals(DetailsState.Loading, awaitItem())
            assertEquals(DetailsState.Error, awaitItem())

            expectNoEvents()
        }
    }
}
