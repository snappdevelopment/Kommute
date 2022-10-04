package com.sebastianneubauer.kommutedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.sebastianneubauer.kommute.Kommute
import com.sebastianneubauer.kommutedemo.network.BitcoinApi
import com.sebastianneubauer.kommutedemo.network.getRetrofit
import com.sebastianneubauer.kommutedemo.ui.theme.KommuteTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val retrofit = getRetrofit()
    private val api = BitcoinApi(retrofit)
    private val kommute = Kommute.Factory.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KommuteTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { kommute.start(this@MainActivity) }
                        ) {
                            Text(text = "Start Kommute")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { loadBitcoinPrice() }
                        ) {
                            Text(text = "Do 3 api calls")
                        }
                    }
                }
            }
        }
    }

    private fun loadBitcoinPrice() {
        lifecycleScope.launch {
            api.getBitcoinPrice()
            delay(3000)
            api.getBitcoinPrice()
            delay(3000)
            api.getBitcoinPrice()
        }
    }
}