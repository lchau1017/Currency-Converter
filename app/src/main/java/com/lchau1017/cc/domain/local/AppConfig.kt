package com.lchau1017.cc.domain.local

interface AppConfig {
    suspend fun getDefaultCurrency(): String
    suspend fun getCountDownTime(): Int
    suspend fun getAvailableCurrency(): Set<String>
}