package com.sebastianneubauer.kommute.feed.navigation

import androidx.navigation.NavGraphBuilder
import com.sebastianneubauer.kommute.navigation.NavigationDestination
import androidx.navigation.compose.composable
import com.sebastianneubauer.kommute.feed.FeedUi
import com.sebastianneubauer.kommute.feed.FeedViewModel

internal object FeedDestination : NavigationDestination {
    override val route = "feed_route"
}

internal fun NavGraphBuilder.feedGraph(
    viewModelFactory: FeedViewModel.Factory,
    onBackClick: () -> Unit,
    onRequestClick: (Long) -> Unit
) {
    composable(route = FeedDestination.route) {
        FeedUi(viewModelFactory, onBackClick, onRequestClick)
    }
}