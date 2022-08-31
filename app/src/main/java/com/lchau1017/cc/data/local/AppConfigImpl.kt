package com.lchau1017.cc.data.local

import com.lchau1017.cc.domain.local.AppConfig
import javax.inject.Inject

private const val TIME = 30

class AppConfigImpl @Inject constructor(
) : AppConfig {
    override suspend fun getDefaultCurrency(): String = "USD"

    override suspend fun getCountDownTime(): Int = TIME

    override suspend fun getAvailableCurrency(): Set<String> =
        setOf(
            "USD", "CAD", "GBP", "HKD", "RUB",
            "SDG", "TOP", "UAH", "XDR", "ZWL"
        )

}