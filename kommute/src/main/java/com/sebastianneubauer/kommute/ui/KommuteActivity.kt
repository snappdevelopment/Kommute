package com.sebastianneubauer.kommute.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sebastianneubauer.kommute.Kommute
import com.sebastianneubauer.kommute.details.DetailsViewModel
import com.sebastianneubauer.kommute.details.navigation.DetailsDestination
import com.sebastianneubauer.kommute.details.navigation.detailsGraph
import com.sebastianneubauer.kommute.feed.FeedViewModel
import com.sebastianneubauer.kommute.feed.navigation.FeedDestination
import com.sebastianneubauer.kommute.feed.navigation.feedGraph
import com.sebastianneubauer.kommute.util.LocalDateTimeFormatter

internal class KommuteActivity : ComponentActivity() {

    private val repository = Kommute.Factory.repository
    private val dateTimeFormatter = LocalDateTimeFormatter()
    private val feedViewModelFactory = FeedViewModel.Factory(repository, dateTimeFormatter)
    private val detailsViewModelFactory = DetailsViewModel.Factory(repository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = FeedDestination.route,
            ) {
                feedGraph(
                    viewModelFactory = feedViewModelFactory,
                    onBackClick = { navController.popBackStack() },
                    onRequestClick = { navController.navigate("${DetailsDestination.route}/$it") }
                )
                detailsGraph(
                    viewModelFactory = detailsViewModelFactory,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
