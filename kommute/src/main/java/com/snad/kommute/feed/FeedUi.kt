package com.snad.kommute.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snad.kommute.R

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
internal fun FeedUi(
    viewModelFactory: FeedViewModel.Factory,
    onBackClick: () -> Unit,
    onRequestClick: (Long) -> Unit
) {
    val viewModel: FeedViewModel = viewModel(factory = viewModelFactory)
    val state: FeedState by viewModel.state.collectAsStateWithLifecycle()

    Feed(
        state = state,
        onRequestClick = onRequestClick,
        onClearClick = viewModel::clearClicked
    )
}

@Composable
private fun Feed(
    state: FeedState,
    onRequestClick: (Long) -> Unit,
    onClearClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        when(state) {
            is Content -> FeedList(state, onRequestClick, onClearClick)
            is Loading -> Loading()
            is Empty -> Empty()
        }
    }
}

@Composable
private fun FeedList(
    state: Content,
    onRequestClick: (Long) -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.End),
            onClick = onClearClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_kommute_delete),
                tint = Color.DarkGray,
                contentDescription = null
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(items = state.requests, key = { _, item -> item.id }) { index, item ->
                Request(item = item, onRequestClick = onRequestClick)

                if(index < state.requests.size - 1) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
private fun Request(
    item: NetworkRequestListItem,
    onRequestClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item !is NetworkRequestListItem.Ongoing) { onRequestClick(item.id) }
            .alpha(alpha = if (item is NetworkRequestListItem.Ongoing) 0.3F else 1F)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Text(
                text = item.dateTime,
                style = MaterialTheme.typography.body2,
                color = Color.DarkGray,
            )

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider()

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.method,
                style = MaterialTheme.typography.body2,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider()

            Spacer(modifier = Modifier.width(8.dp))

            when(item) {
                is NetworkRequestListItem.Ongoing -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(14.dp),
                        color = Color.DarkGray,
                        strokeWidth = 2.dp
                    )
                }
                is NetworkRequestListItem.Finished -> {
                    Text(
                        text = item.statusCode.toString(),
                        style = MaterialTheme.typography.body2,
                        color = if(item.statusCode >= 400) Color.Red else Color.DarkGray
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    VerticalDivider()

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.duration,
                        style = MaterialTheme.typography.body2,
                        color = Color.DarkGray
                    )
                }
                is NetworkRequestListItem.Failed -> {}
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.url,
            style = MaterialTheme.typography.body1,
            color = Color.Black,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        if(item is NetworkRequestListItem.Failed) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.errorMessage,
                style = MaterialTheme.typography.body2,
                color = Color.Red,
            )
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
private fun BoxScope.Empty() {
    Text(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = 16.dp),
        text = stringResource(R.string.kommute_feed_state_empty),
        style = MaterialTheme.typography.h5,
        color = Color.Black.copy(alpha = 0.3f),
    )
}

@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp),
        color = Color.DarkGray
    )
}

@Preview
@Composable
private fun FeedPreview() {
    Feed(
        state = Content(
            requests = listOf(
                NetworkRequestListItem.Ongoing(
                    id = 0L,
                    dateTime = "15:13:24",
                    method = "GET",
                    url = "www.example.com/api?id=5&text=Lorem ipsum dolor sit amet, consectetur adipiscing" +
                            " elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                ),
                NetworkRequestListItem.Finished(
                    id = 0L,
                    dateTime = "15:13:24",
                    method = "GET",
                    statusCode = 200,
                    duration = "25ms",
                    url = "www.example.com/api?id=5&text=Lorem ipsum dolor sit amet, consectetur adipiscing " +
                            "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                ),
                NetworkRequestListItem.Failed(
                    id = 0L,
                    dateTime = "15:13:24",
                    method = "GET",
                    url = "www.example.com/api?id=5&text=Lorem ipsum dolor sit amet, consectetur adipiscing" +
                            " elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                    errorMessage = "This is an error message, because the api call failed",
                ),
            )
        ),
        onRequestClick = {},
        onClearClick = {}
    )
}

@Composable
@Preview
private fun EmptyPreview() {
    Feed(
        state = Empty,
        onRequestClick = {},
        onClearClick = {}
    )
}

@Composable
@Preview
private fun LoadingPreview() {
    Feed(
        state = Loading,
        onRequestClick = {},
        onClearClick = {}
    )
}