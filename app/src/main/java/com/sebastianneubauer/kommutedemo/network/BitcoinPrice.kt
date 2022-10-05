package com.sebastianneubauer.kommutedemo.network

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class BitcoinPrice(
    @Json(name = "bpi")
    val bpi: Bpi,
    @Json(name = "chartName")
    val chartName: String,
    @Json(name = "disclaimer")
    val disclaimer: String,
    @Json(name = "time")
    val time: Time
)

@JsonClass(generateAdapter = true)
data class Bpi(
    @Json(name = "EUR")
    val eUR: EUR,
    @Json(name = "GBP")
    val gBP: GBP,
    @Json(name = "USD")
    val uSD: USD
)

@JsonClass(generateAdapter = true)
data class Time(
    @Json(name = "updated")
    val updated: String,
    @Json(name = "updatedISO")
    val updatedISO: String,
    @Json(name = "updateduk")
    val updateduk: String
)

@JsonClass(generateAdapter = true)
data class EUR(
    @Json(name = "code")
    val code: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "rate")
    val rate: String,
    @Json(name = "rate_float")
    val rateFloat: Double,
    @Json(name = "symbol")
    val symbol: String
)

@JsonClass(generateAdapter = true)
data class GBP(
    @Json(name = "code")
    val code: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "rate")
    val rate: String,
    @Json(name = "rate_float")
    val rateFloat: Double,
    @Json(name = "symbol")
    val symbol: String
)

@JsonClass(generateAdapter = true)
data class USD(
    @Json(name = "code")
    val code: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "rate")
    val rate: String,
    @Json(name = "rate_float")
    val rateFloat: Double,
    @Json(name = "symbol")
    val symbol: String
)