package com.sebastianneubauer.kommute

import app.cash.turbine.test
import com.sebastianneubauer.kommute.feed.FeedState.Content
import com.sebastianneubauer.kommute.feed.FeedState.Empty
import com.sebastianneubauer.kommute.feed.FeedState.Loading
import com.sebastianneubauer.kommute.feed.FeedViewModel
import com.sebastianneubauer.kommute.feed.NetworkRequestListItem
import com.sebastianneubauer.kommute.helper.FakeDateTimeFormatter
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
internal class FeedViewModelTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeNetworkDataRepository()
    private val dateTimeFormatter = FakeDateTimeFormatter()

    private val underTest by lazy {
        FeedViewModel(
            repository = repository,
            dateTimeFormatter = dateTimeFormatter,
            defaultDispatcher = UnconfinedTestDispatcher()
        )
    }

    private val finishedRequest = NetworkRequest.Finished(
        id = 0,
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

    private val ongoingRequest = NetworkRequest.Ongoing(
        id = 0,
        timestampMillis = 0,
        url = "",
        method = "",
        requestBody = null,
        requestHeaders = mapOf(),
    )

    private val failedRequest = NetworkRequest.Failed(
        id = 0,
        timestampMillis = 0,
        url = "",
        method = "",
        requestBody = null,
        requestHeaders = mapOf(),
        errorMessage = ""
    )

    private val finishedRequests = listOf(finishedRequest, finishedRequest.copy(id = 1))

    @Test
    fun `initial state is empty`() = runTest {
        underTest.state.test {
            assertEquals(Loading, awaitItem())
            repository.setRequests(emptyList())
            assertEquals(Empty, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `finished request is finished in state`() = runTest {
        underTest.state.test {
            assertEquals(Loading, awaitItem())
            repository.setRequests(listOf(finishedRequest))

            val contentState = Content(
                listOf(
                    NetworkRequestListItem.Finished(
                        id = 0,
                        dateTime = "",
                        url = "",
                        method = "",
                        duration = "0ms",
                        statusCode = 0
                    ),
                )
            )

            assertEquals(contentState, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `ongoing request is ongoing in state`() = runTest {
        underTest.state.test {
            assertEquals(Loading, awaitItem())
            repository.setRequests(listOf(ongoingRequest))

            val contentState = Content(
                listOf(NetworkRequestListItem.Ongoing(id = 0, dateTime = "", url = "", method = ""))
            )

            assertEquals(contentState, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `failed request is failed in state`() = runTest {
        underTest.state.test {
            assertEquals(Loading, awaitItem())
            repository.setRequests(listOf(failedRequest))

            val contentState = Content(
                listOf(
                    NetworkRequestListItem.Failed(id = 0, dateTime = "", url = "", method = "", errorMessage = ""),
                )
            )

            assertEquals(contentState, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `network requests are shown in reverse order`() = runTest {
        underTest.state.test {
            assertEquals(Loading, awaitItem())

            repository.setRequests(finishedRequests)

            val contentState = Content(
                listOf(
                    NetworkRequestListItem.Finished(
                        id = 1,
                        dateTime = "",
                        url = "",
                        method = "",
                        duration = "0ms",
                        statusCode = 0
                    ),
                    NetworkRequestListItem.Finished(
                        id = 0,
                        dateTime = "",
                        url = "",
                        method = "",
                        duration = "0ms",
                        statusCode = 0
                    ),
                )
            )

            assertEquals(contentState, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `updating a network request updates the state`() = runTest {
        underTest.state.test {
            assertEquals(Loading, awaitItem())

            repository.setRequests(listOf(ongoingRequest))

            val ongoingState = Content(
                listOf(NetworkRequestListItem.Ongoing(id = 0, dateTime = "", url = "", method = ""))
            )

            assertEquals(ongoingState, awaitItem())

            repository.setRequests(listOf(finishedRequest))

            val finishedState = Content(
                listOf(
                    NetworkRequestListItem.Finished(
                        id = 0,
                        dateTime = "",
                        url = "",
                        method = "",
                        duration = "0ms",
                        statusCode = 0
                    )
                )
            )

            assertEquals(finishedState, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `clear button clears all requests`() = runTest {
        underTest.state.test {
            assertEquals(Loading, awaitItem())

            repository.setRequests(listOf(finishedRequest))

            val contentState = Content(
                listOf(
                    NetworkRequestListItem.Finished(
                        id = 0,
                        dateTime = "",
                        url = "",
                        method = "",
                        duration = "0ms",
                        statusCode = 0
                    ),
                )
            )

            assertEquals(contentState, awaitItem())

            underTest.clearClicked()

            assertEquals(Empty, awaitItem())

            expectNoEvents()
        }
    }
}
