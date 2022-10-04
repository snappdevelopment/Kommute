package com.sebastianneubauer.kommutedemo.network

import retrofit2.Retrofit
import retrofit2.http.GET

internal class BitcoinApi(
    private val retrofit: Retrofit
) {

    private val service = retrofit.create(BitcoinPriceService::class.java)

    suspend fun getBitcoinPrice(): ApiResult {
        return try {
            service.getBitcoinPrice()
            ApiResult.Success
        }
        catch (error: Exception) {
            ApiResult.Error
        }
    }
}

internal sealed class ApiResult {
    object Success: ApiResult()
    object Error: ApiResult()
}

internal interface BitcoinPriceService {
    @GET("v1/bpi/currentprice.json")
    suspend fun getBitcoinPrice(): BitcoinPrice
}