package com.lchau1017.cc.ui.calculate

import com.lchau1017.cc.CoroutineTestRule
import com.lchau1017.cc.collectForTesting
import com.lchau1017.cc.domain.error.ApiErrorException
import com.lchau1017.cc.domain.model.ConvertResult
import com.lchau1017.cc.domain.usecase.GetRatesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalculateAmountViewModelTest {

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    private val getRatesUseCase: GetRatesUseCase =
        mockk(relaxed = true)

    private lateinit var testedClass: CalculateAmountViewModel

    @Test
    fun `when get initial viewModel state then state is the default state`() =
        runTest {
            //given
            testedClass = CalculateAmountViewModel(
                getRatesUseCase,
                coroutineTestRule.testDispatcher
            )
            // when // then
            testedClass.state.collectForTesting {
                assertEquals(UiState.Loading, awaitItem())
            }
        }

    @Test
    fun `given getRatesLabels return success  when calculateAmountViewModel getRatesLabels then show InitData`() =
        runTest {
            //given
            testedClass = CalculateAmountViewModel(
                getRatesUseCase,
                coroutineTestRule.testDispatcher
            )

            coEvery { getRatesUseCase.getRatesLabels() } returns flow {
                emit(
                    Result.success(listOf("USD", "HKD"))
                )
            }

            testedClass.state.collectForTesting {

                // when
                testedClass.getRatesLabels()

                // then
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(
                    UiState.InitData(listOf("USD", "HKD"), listOf("USD", "HKD")),
                    awaitItem()
                )

            }
        }


    @Test
    fun `given getRatesUseCase calculate return success  when calculateAmountViewModel calculate then emit ToConfirmScreen`() =
        runTest {
            //given
            testedClass = CalculateAmountViewModel(
                getRatesUseCase,
                coroutineTestRule.testDispatcher
            )

            val amount = 100
            val base = "USD"
            val toCurrency = "HKD"
            val rate = 7.85f
            val toValue = 7.85 * 100

            coEvery { getRatesUseCase.convert(amount.toString(), base, toCurrency) } returns flow {
                emit(
                    Result.success(
                        ConvertResult(
                            "$amount $base",
                            "$toValue $toCurrency",
                            "1 / $rate"
                        )
                    )
                )
            }

            testedClass.effect.collectForTesting {

                // when
                testedClass.calculate(amount.toString(), base, toCurrency)

                // then
                assertEquals(
                    Effect.ToConfirmScreen(
                        "$amount $base",
                        "$toValue $toCurrency",
                        "1 / $rate"
                    ), awaitItem()
                )

            }
        }


    @Test
    fun `given getRatesLabels return error  when calculateAmountViewModel getRatesLabels then emit ShowError`() =
        runTest {
            //given
            testedClass = CalculateAmountViewModel(
                getRatesUseCase,
                coroutineTestRule.testDispatcher
            )

            coEvery { getRatesUseCase.getRatesLabels() } returns flow {
                emit(
                    Result.failure(ApiErrorException())
                )
            }

            testedClass.effect.collectForTesting {

                // when
                testedClass.getRatesLabels()

                // then
                assertEquals(ApiErrorException().message?.let { Effect.ShowError(it) }, awaitItem())

            }
        }


}