package com.sebastianneubauer.kommute.details.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sebastianneubauer.kommute.details.DetailsUi
import com.sebastianneubauer.kommute.details.DetailsViewModel
import com.sebastianneubauer.kommute.navigation.NavigationDestination

internal object DetailsDestination : NavigationDestination {
    override val route = "details_route"
    const val requestId = "requestId"
}

internal fun NavGraphBuilder.detailsGraph(
    viewModelFactory: DetailsViewModel.Factory,
    onBackClick: () -> Unit
) {
    composable(
        route = "${DetailsDestination.route}/{${DetailsDestination.requestId}}",
        arguments = listOf(navArgument(DetailsDestination.requestId) { type = NavType.LongType })
    ) { backStackEntry ->
        val requestId = backStackEntry.arguments?.getLong(DetailsDestination.requestId)
        if (requestId == null) {
            onBackClick()
        }

        DetailsUi(requestId!!, viewModelFactory, onBackClick)
    }
}
