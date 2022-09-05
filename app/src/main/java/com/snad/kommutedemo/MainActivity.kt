package com.snad.kommutedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.snad.kommute.Kommute
import com.snad.kommutedemo.network.BitcoinApi
import com.snad.kommutedemo.network.getRetrofit
import com.snad.kommutedemo.ui.theme.KommuteTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val retrofit = getRetrofit()
    private val api = BitcoinApi(retrofit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kommute = Kommute.Factory.get()
        kommute.start(this)

        setContent {
            KommuteTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                ) {
                   Button(
                       modifier = Modifier.align(Alignment.Center),
                       onClick = { loadBitcoinPrice() }
                   ) {
                       Text(text = "Make api call")
                   }
                }
            }
        }
    }

    private fun loadBitcoinPrice() {
        lifecycleScope.launch {
            api.getBitcoinPrice()
        }
    }
}