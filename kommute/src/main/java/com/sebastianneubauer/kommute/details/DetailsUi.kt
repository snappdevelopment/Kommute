package com.sebastianneubauer.kommute.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import com.sebastianneubauer.jsontree.JsonTree
import com.sebastianneubauer.jsontree.TreeState
import com.sebastianneubauer.kommute.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
internal fun DetailsUi(
    requestId: Long,
    viewModelFactory: DetailsViewModel.Factory,
    imageLoader: ImageLoader,
    onBackClicked: () -> Unit
) {
    val viewModel: DetailsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state(requestId).collectAsStateWithLifecycle()

    Details(state, imageLoader, onBackClicked)
}

@Composable
private fun Details(
    state: DetailsState,
    imageLoader: ImageLoader,
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

            Box(modifier = Modifier.fillMaxSize()) {
                when (state) {
                    is DetailsState.Initial -> Loading()
                    is DetailsState.Content -> Content(
                        state.networkRequestDetailsItem,
                        imageLoader
                    )
                    is DetailsState.Error -> Error()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    networkRequestDetailsItem: NetworkRequestDetailsItem,
    imageLoader: ImageLoader,
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(SelectedTab.RESPONSE) }
    val pagerState = rememberPagerState(
        initialPage = selectedTab.index,
        pageCount = { SelectedTab.values().size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { selectedTab = SelectedTab.getTabForIndex(it) }
    }

    Column {
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            contentColor = Color.DarkGray,
            selectedTabIndex = selectedTab.index,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.index]),
                    color = Color.DarkGray
                )
            }
        ) {
            PagerTab(
                title = stringResource(R.string.kommute_details_tab_title_response),
                selected = selectedTab == SelectedTab.RESPONSE,
                onClick = {
                    selectedTab = SelectedTab.RESPONSE
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(SelectedTab.RESPONSE.index)
                    }
                }
            )

            PagerTab(
                title = stringResource(R.string.kommute_details_tab_title_request),
                selected = selectedTab == SelectedTab.REQUEST,
                onClick = {
                    selectedTab = SelectedTab.REQUEST
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(SelectedTab.REQUEST.index)
                    }
                }
            )

            PagerTab(
                title = stringResource(R.string.kommute_details_tab_title_headers),
                selected = selectedTab == SelectedTab.HEADERS,
                onClick = {
                    selectedTab = SelectedTab.HEADERS
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(SelectedTab.HEADERS.index)
                    }
                }
            )
        }

        HorizontalPager(
            beyondBoundsPageCount = 2,
            state = pagerState
        ) { pageIndex ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (pageIndex) {
                    0 -> ResponseTab(networkRequestDetailsItem, imageLoader)
                    1 -> RequestTab(networkRequestDetailsItem.requestBody)
                    2 -> HeadersTab(
                        url = networkRequestDetailsItem.url,
                        requestHeaders = networkRequestDetailsItem.requestHeaders,
                        responseHeaders = networkRequestDetailsItem.responseHeaders
                    )
                }
            }
        }
    }
}

private enum class SelectedTab(val index: Int) {
    RESPONSE(0),
    REQUEST(1),
    HEADERS(2);

    companion object {
        fun getTabForIndex(index: Int): SelectedTab {
            return when (index) {
                0 -> RESPONSE
                1 -> REQUEST
                2 -> HEADERS
                else -> RESPONSE
            }
        }
    }
}

@Composable
private fun ResponseTab(
    item: NetworkRequestDetailsItem,
    imageLoader: ImageLoader,
) {
    Column {
        Headline(text = stringResource(R.string.kommute_details_headline_response_body))

        Spacer(modifier = Modifier.height(8.dp))

        val responseText = when {
            item.responseBody != null -> item.responseBody
            !item.isImage -> stringResource(R.string.kommute_details_response_body_empty)
            else -> null
        }

        var isNotJson by remember(responseText) { mutableStateOf(false) }

        JsonTree(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            initialState = TreeState.EXPANDED,
            json = responseText ?: "",
            textStyle = MaterialTheme.typography.bodyMedium,
            onError = { isNotJson = true },
            onLoading = {
                Text(text = stringResource(R.string.kommute_details_body_loading))
            }
        )

        if (responseText != null && isNotJson) {
            Text(
                text = responseText,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (item.isImage) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F),
                model = item.url,
                imageLoader = imageLoader,
                alignment = Alignment.TopStart,
                contentDescription = null,
                loading = { ImagePlaceholder() },
                error = { ImagePlaceholder() }
            )
        }
    }
}

@Composable
private fun RequestTab(
    requestBody: String?
) {
    Column {
        Headline(text = stringResource(R.string.kommute_details_headline_request_body))

        Spacer(modifier = Modifier.height(8.dp))

        var isNotJson by remember(requestBody) { mutableStateOf(false) }

        JsonTree(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            initialState = TreeState.EXPANDED,
            json = requestBody ?: "",
            textStyle = MaterialTheme.typography.bodyMedium,
            onError = { isNotJson = true },
            onLoading = {
                Text(text = stringResource(R.string.kommute_details_body_loading))
            }
        )

        if (isNotJson) {
            Text(
                text = requestBody ?: stringResource(R.string.kommute_details_request_body_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
private fun HeadersTab(
    url: String,
    requestHeaders: Map<String, String>,
    responseHeaders: Map<String, String>?
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Headline(text = stringResource(R.string.kommute_details_headline_url))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = url,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = stringResource(R.string.kommute_details_headline_request_headers))

        Spacer(modifier = Modifier.height(8.dp))

        Headers(headers = requestHeaders)

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = stringResource(R.string.kommute_details_headline_response_headers))

        Spacer(modifier = Modifier.height(8.dp))

        Headers(headers = responseHeaders)
    }
}

@Composable
private fun PagerTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        text = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        },
        selected = selected,
        onClick = onClick
    )
}

@Composable
private fun Headline(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black
    )
}

@Composable
private fun Headers(
    headers: Map<String, String>?
) {
    if (headers.isNullOrEmpty()) {
        Text(
            text = stringResource(R.string.kommute_details_headers_empty),
            style = MaterialTheme.typography.bodyMedium,
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    modifier = Modifier.weight(1F),
                    text = entry.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }

            if (index < headers.size - 1) {
                Spacer(modifier = Modifier.height(4.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun ImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(0.5f)
                .align(Alignment.Center),
            painter = rememberVectorPainter(
                ImageVector.vectorResource(R.drawable.ic_kommute_icon)
            ),
            alpha = 0.2f,
            contentDescription = null
        )
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
        style = MaterialTheme.typography.headlineSmall,
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
                            "isActive": false
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
                            "isActive": false
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
        imageLoader = ImageLoader(LocalContext.current),
        onBackClicked = {}
    )
}

@Composable
@Preview
private fun ErrorPreview() {
    Details(
        state = DetailsState.Error,
        imageLoader = ImageLoader(LocalContext.current),
        onBackClicked = {}
    )
}

@Composable
@Preview
private fun LoadingPreview() {
    Details(
        state = DetailsState.Initial,
        imageLoader = ImageLoader(LocalContext.current),
        onBackClicked = {}
    )
}
