package com.lchau1017.cc.domain.usecase

import com.lchau1017.cc.domain.error.InvalidInputException
import com.lchau1017.cc.domain.local.AppConfig
import com.lchau1017.cc.domain.model.ConvertResult
import com.lchau1017.cc.domain.repository.RatesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

class GetRatesUseCase @Inject constructor(
    private val ratesRepository: RatesRepository,
    private val appConfig: AppConfig,
    @Named("io") private val dispatcher: CoroutineDispatcher
) {
    suspend fun getRatesLabels(): Flow<Result<Pair<List<String>, List<String>>>> = flow {
        val base = appConfig.getDefaultCurrency()
        val fromLabels = ratesRepository.getRates(base)
            .keys
            .intersect(appConfig.getAvailableCurrency())
            .toList()
        val toLabels = fromLabels.filter { rates -> rates != base }
        emit(
            Result.success(Pair(fromLabels, toLabels))
        )
    }.flowOn(dispatcher)
        .catch {
            emit(Result.failure(it))
        }

    suspend fun convert(amount: String, base: String, to: String): Flow<Result<ConvertResult>> =
        flow {
            if (amount.isEmpty()) throw InvalidInputException()
            if (amount.toFloat() <= 0) throw InvalidInputException()
            val ratesMap = ratesRepository.getRates(base)
            val rate = ratesMap[to]
            val toValue = rate?.times(amount.toFloat())
            emit(Result.success(ConvertResult("$amount $base", "$toValue $to", "1 / $rate")))
        }.flowOn(dispatcher)
            .catch {
                emit(Result.failure(it))
            }
}