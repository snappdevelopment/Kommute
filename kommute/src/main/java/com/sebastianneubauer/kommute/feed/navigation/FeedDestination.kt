package com.sebastianneubauer.kommute.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianneubauer.kommute.feed.FeedUi
import com.sebastianneubauer.kommute.feed.FeedViewModel
import com.sebastianneubauer.kommute.navigation.NavigationDestination

internal object FeedDestination : NavigationDestination {
    override val route = "feed_route"
}

internal fun NavGraphBuilder.feedGraph(
    viewModelFactory: FeedViewModel.Factory,
    onRequestClick: (Long) -> Unit
) {
    composable(route = FeedDestination.route) {
        FeedUi(viewModelFactory, onRequestClick)
    }
}
