package com.snad.kommute.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.snad.kommute.Kommute
import com.snad.kommute.details.DetailsViewModel
import com.snad.kommute.details.navigation.DetailsDestination
import com.snad.kommute.details.navigation.detailsGraph
import com.snad.kommute.feed.FeedViewModel
import com.snad.kommute.feed.navigation.FeedDestination
import com.snad.kommute.feed.navigation.feedGraph
import com.snad.kommute.util.LocalDateTimeFormatter

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
