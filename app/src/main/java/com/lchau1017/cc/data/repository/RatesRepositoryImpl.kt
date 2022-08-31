package com.lchau1017.cc.data.repository

import com.lchau1017.cc.data.remote.Api
import com.lchau1017.cc.domain.repository.RatesRepository
import com.lchau1017.cc.domain.error.ApiErrorException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

private const val RESULT_SUCCESS = "success"
private const val RESULT_ERROR = "error"

class RatesRepositoryImpl @Inject constructor(
    private val api: Api,
    @Named("io") private val dispatcher: CoroutineDispatcher
) : RatesRepository {

    override suspend fun getRates(base: String): Map<String, Float> = withContext(dispatcher) {
        when (api.getRates(base).result) {
            RESULT_SUCCESS -> {
                return@withContext api.getRates(base).rates
            }
            RESULT_ERROR -> {
                throw ApiErrorException()
            }
            else -> return@withContext emptyMap()
        }
    }

}