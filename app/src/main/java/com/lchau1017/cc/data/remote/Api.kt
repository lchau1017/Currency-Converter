package com.lchau1017.cc.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("v6/d36b755ed327566bbf136d8f/latest/{rates}")
    suspend fun getRates(@Path("rates") rates: String): LatestRatesResponse
}