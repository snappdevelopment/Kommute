package com.snad.kommute.details

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snad.kommute.R

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
internal fun DetailsUi(
    requestId: Long,
    viewModelFactory: DetailsViewModel.Factory,
    onBackClicked: () -> Unit
) {
    val viewModel: DetailsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state(requestId).collectAsStateWithLifecycle()

    Details(state, onBackClicked)
}

@Composable
private fun Details(
    state: DetailsState,
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_kommute_arrow_back),
                    contentDescription = null
                )
            }

            Box(modifier = Modifier.fillMaxSize()){
                when(val currentState = state) {
                    is DetailsState.Initial -> Loading()
                    is DetailsState.Content -> Content(currentState.networkRequestDetailsItem)
                    is DetailsState.Error -> Error()
                }
            }
        }
    }
}

@Composable
private fun Content(
    networkRequestDetailsItem: NetworkRequestDetailsItem
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Headline(text = stringResource(R.string.kommute_details_headline_url))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = networkRequestDetailsItem.url,
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = stringResource(R.string.kommute_details_headline_request_headers))

        Spacer(modifier = Modifier.height(8.dp))

        Headers(headers = networkRequestDetailsItem.requestHeaders)

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = stringResource(R.string.kommute_details_headline_request_body))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 2),
            text = networkRequestDetailsItem.requestBody ?: stringResource(R.string.kommute_details_request_body_empty),
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = stringResource(R.string.kommute_details_headline_response_headers))

        Spacer(modifier = Modifier.height(8.dp))

        Headers(headers = networkRequestDetailsItem.responseHeaders)

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = stringResource(R.string.kommute_details_headline_response_body))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 2),
            text = networkRequestDetailsItem.responseBody ?: stringResource(R.string.kommute_details_response_body_empty),
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun Headline(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        color = Color.Black
    )
}

@Composable
private fun Headers(
    headers: Map<String, String>?
) {
    if(headers.isNullOrEmpty()) {
        Text(
            text = stringResource(R.string.kommute_details_headers_empty),
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )
    } else {
        headers.entries.forEachIndexed { index, entry ->
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = entry.key,
                    style = MaterialTheme.typography.body2,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    modifier = Modifier.weight(1F),
                    text = entry.value,
                    style = MaterialTheme.typography.body2,
                    color = Color.DarkGray
                )
            }

            if(index < headers.size - 1) {
                Spacer(modifier = Modifier.height(4.dp))

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun BoxScope.Loading() {
    CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        color = Color.Black.copy(alpha = 0.3f)
    )
}

@Composable
private fun BoxScope.Error() {
    Text(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = 16.dp),
        text = stringResource(R.string.kommute_details_state_error),
        style = MaterialTheme.typography.h5,
        color = Color.Black.copy(alpha = 0.3f),
    )
}

@Composable
@Preview
private fun ContentPreview() {
    Details(
        state = DetailsState.Content(
            networkRequestDetailsItem = NetworkRequestDetailsItem(
                url = "www.example.com",
                requestBody = """
                {
                    "data": [
                        {
                            "id": "62d19ae39874bff5d95efb89",
                            "index": 0,
                            "isActive": false,
                        }
                    ]
                }
            """.trimIndent(),
                responseBody = """
                {
                    "data": [
                        {
                            "id": "62d19ae39874bff5d95efb89",
                            "index": 0,
                            "isActive": false,
                        }
                    ]
                }
            """.trimIndent(),
                requestHeaders = mapOf(
                    "Content-Type" to "image/jpeg",
                    "Accept-Encoding" to "gzip"
                ),
                responseHeaders = mapOf(
                    "Content-Type" to "image/jpeg",
                    "Accept-Encoding" to "gzip"
                ),
            )
        ),
        onBackClicked = {}
    )
}

@Composable
@Preview
private fun ErrorPreview() {
    Details(state = DetailsState.Error, onBackClicked = {})
}

@Composable
@Preview
private fun LoadingPreview() {
    Details(state = DetailsState.Initial, onBackClicked = {})
}