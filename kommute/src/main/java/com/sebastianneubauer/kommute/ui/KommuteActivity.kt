package com.sebastianneubauer.kommute.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.sebastianneubauer.kommute.Kommute
import com.sebastianneubauer.kommute.details.DetailsViewModel
import com.sebastianneubauer.kommute.details.navigation.DetailsDestination
import com.sebastianneubauer.kommute.details.navigation.detailsGraph
import com.sebastianneubauer.kommute.feed.FeedViewModel
import com.sebastianneubauer.kommute.feed.navigation.FeedDestination
import com.sebastianneubauer.kommute.feed.navigation.feedGraph
import com.sebastianneubauer.kommute.util.LocalDateTimeFormatter
import com.sebastianneubauer.kommute.util.localImageLoader

internal class KommuteActivity : ComponentActivity() {

    private val repository = Kommute.repository
    private val dateTimeFormatter = LocalDateTimeFormatter()
    private val feedViewModelFactory = FeedViewModel.Factory(repository, dateTimeFormatter)
    private val detailsViewModelFactory = DetailsViewModel.Factory(repository)
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader = localImageLoader(applicationContext)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = FeedDestination.route,
            ) {
                feedGraph(
                    viewModelFactory = feedViewModelFactory,
                    onRequestClick = { navController.navigate("${DetailsDestination.route}/$it") }
                )
                detailsGraph(
                    viewModelFactory = detailsViewModelFactory,
                    imageLoader = imageLoader,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
