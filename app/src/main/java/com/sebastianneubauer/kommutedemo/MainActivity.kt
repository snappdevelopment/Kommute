package com.sebastianneubauer.kommutedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.imageLoader
import com.sebastianneubauer.kommute.Kommute
import com.sebastianneubauer.kommutedemo.network.ProductsApi
import com.sebastianneubauer.kommutedemo.network.getRetrofit
import com.sebastianneubauer.kommutedemo.ui.theme.KommuteTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class MainActivity : ComponentActivity() {

    private val kommute = Kommute.getInstance()
    private lateinit var api: ProductsApi

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = getRetrofit(this)
        api = ProductsApi(retrofit)

        setContent {
            KommuteTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { kommute.start(this@MainActivity) }
                    ) {
                        Text(text = "Start Kommute")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { loadProducts() }
                    ) {
                        Text(text = "Make 3 api calls")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { makeNotFoundCall() }
                    ) {
                        Text(text = "Make 404 error api call")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    var showImage by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            applicationContext.imageLoader.diskCache?.clear()
                            showImage = !showImage
                        }
                    ) {
                        Text(text = if(showImage) "Hide image" else "Load image")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if(showImage) {
                        AsyncImage(
                            modifier = Modifier.fillMaxWidth(),
                            model = "https://picsum.photos/1920/1080",
                            contentDescription = null
                        )
                    }

                    var showGif by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            applicationContext.imageLoader.diskCache?.clear()
                            showGif = !showGif
                        }
                    ) {
                        Text(text = if(showGif) "Hide gif" else "Load gif")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if(showGif) {
                        AsyncImage(
                            modifier = Modifier.fillMaxWidth(),
                            model = "https://media.giphy.com/media/gw3IWyGkC0rsazTi/giphy.gif",
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            api.getProducts()
            delay(3000)
            api.getProducts()
            delay(3000)
            api.getProducts()
        }
    }

    private fun makeNotFoundCall() {
        lifecycleScope.launch {
            api.getNonExistent()
        }
    }
}