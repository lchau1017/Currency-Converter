package com.lchau1017.cc.domain.usecase

import com.lchau1017.cc.domain.local.AppConfig
import javax.inject.Inject

class CountDownUseCase @Inject constructor(
    private val appConfig: AppConfig,
) {
    suspend fun getCountDownTime(): Int = appConfig.getCountDownTime()


}