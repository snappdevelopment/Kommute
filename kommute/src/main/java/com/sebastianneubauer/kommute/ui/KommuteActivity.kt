package com.sebastianneubauer.kommute.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
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
import kotlinx.coroutines.Dispatchers

internal val LocalKommuteImageLoader = staticCompositionLocalOf<ImageLoader> { error("localImageLoader not found") }

internal class KommuteActivity : ComponentActivity() {

    private val defaultDispatcher = Dispatchers.Default
    private val repository = Kommute.repository
    private val dateTimeFormatter = LocalDateTimeFormatter()
    private val feedViewModelFactory = FeedViewModel.Factory(
        repository = repository,
        dateTimeFormatter = dateTimeFormatter,
        defaultDispatcher = defaultDispatcher,
    )
    private val detailsViewModelFactory = DetailsViewModel.Factory(
        repository = repository,
        defaultDispatcher = defaultDispatcher,
    )
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader = localImageLoader(applicationContext)

        setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalKommuteImageLoader provides imageLoader
            ) {
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
                        onBackClick = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
