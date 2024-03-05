package com.sebastianneubauer.kommutedemo.network

import android.util.Log
import retrofit2.Retrofit
import retrofit2.http.GET

internal class ProductsApi(
    private val retrofit: Retrofit
) {

    private val service = retrofit.create(ProductsService::class.java)

    suspend fun getProducts() {
        try {
            service.getProducts()
        } catch (error: Exception) {
            Log.d("ProductApi", "Api call failed")
        }
    }

    suspend fun getNonExistent() {
        try {
            service.getNonExistent()
        } catch (error: Exception) {
            Log.d("ProductApi", "Api call failed")
        }
    }
}

internal interface ProductsService {
    @GET("products?limit=100")
    suspend fun getProducts()

    @GET("non-existent")
    suspend fun getNonExistent()
}