package com.lchau1017.cc.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatestRatesResponse(
    @Json(name = "result")
    val result: String,
    @Json(name = "base_code")
    val base: String,
    @Json(name = "conversion_rates")
    val rates: Map<String, Float>
)
