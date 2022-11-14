package com.lchau1017.cc.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("v6/1a564259a42ac53370a87fab/latest/{rates}")
    suspend fun getRates(@Path("rates") rates: String): LatestRatesResponse
}