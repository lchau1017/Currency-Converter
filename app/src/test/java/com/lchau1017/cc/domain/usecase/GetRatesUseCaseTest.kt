package com.lchau1017.cc.domain.usecase

import app.cash.turbine.test
import com.lchau1017.cc.domain.error.InvalidInputException
import com.lchau1017.cc.domain.local.AppConfig
import com.lchau1017.cc.domain.model.ConvertResult
import com.lchau1017.cc.domain.repository.RatesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetRatesUseCaseTest {

    private val repository: RatesRepository = mockk(relaxed = true)
    private val appConfig: AppConfig = mockk(relaxed = true)

    private lateinit var testedClass: GetRatesUseCase

    @Before
    fun setUp() {
        testedClass = GetRatesUseCase(
            ratesRepository = repository,
            appConfig = appConfig,
            dispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `given amount is empty, when getRatesUseCase convert, then throw InvalidInputException`() =
        runTest {
            val expectedResult = Result.failure<ConvertResult>(InvalidInputException())
            testedClass.convert("", "", "").test {
                assertEquals(
                    expectedResult.exceptionOrNull()?.message,
                    awaitItem().exceptionOrNull()?.message
                )
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `given amount equals 0, when getRatesUseCase convert, then throw InvalidInputException`() =
        runTest {
            val expectedResults = Result.failure<ConvertResult>(InvalidInputException())
            testedClass.convert("0", "", "").test {
                assertEquals(
                    expectedResults.exceptionOrNull()?.message,
                    awaitItem().exceptionOrNull()?.message
                )
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `given amount bigger than 0, when getRatesUseCase convert, then throw InvalidInputException`() =
        runTest {

            val amount = 100
            val base = "USD"
            val toCurrency = "HKD"
            val rate = 7.85f
            val toValue = 7.85 * 100

            val expectedResults =
                ConvertResult("$amount $base", "$toValue $toCurrency", "1 / $rate")
            coEvery { repository.getRates(base) } returns mapOf(toCurrency to rate)

            testedClass.convert("100", "USD", "HKD").test {
                assertEquals(
                    expectedResults,
                    awaitItem().getOrNull()
                )
                cancelAndConsumeRemainingEvents()
            }
        }


    @Test
    fun `given appConfig only has USD and HKD available, when getRatesUseCase getRatesLabels 3 rates, then return USD and HKD for from list and HKD for to list`() =
        runTest {

            val base = "USD"
            coEvery { appConfig.getDefaultCurrency() } returns base
            coEvery { appConfig.getAvailableCurrency() } returns setOf(base, "HKD")
            coEvery { repository.getRates(base) } returns mapOf(
                "USD" to 1f,
                "HKD" to 7.85f,
                "GBP" to 0.78f
            )

            val expectedResults = Pair(listOf("USD", "HKD"), listOf("HKD"))

            testedClass.getRatesLabels().test {
                assertEquals(
                    expectedResults,
                    awaitItem().getOrNull()
                )
                cancelAndConsumeRemainingEvents()
            }
        }
}