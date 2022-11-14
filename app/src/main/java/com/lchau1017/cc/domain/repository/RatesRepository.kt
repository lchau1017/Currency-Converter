package com.lchau1017.cc.domain.repository

interface RatesRepository {
    suspend fun getRates(base: String): Map<String, Float>
}
