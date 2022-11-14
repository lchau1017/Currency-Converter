package com.lchau1017.cc.data.repository

import com.lchau1017.cc.data.remote.Api
import com.lchau1017.cc.data.remote.LatestRatesResponse
import com.lchau1017.cc.domain.error.ApiErrorException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RatesRepositoryImplTest {

    private val apiMock: Api = mockk(relaxed = true)

    private lateinit var testedClass: RatesRepositoryImpl

    @Before
    fun setUp() {
        testedClass = RatesRepositoryImpl(dispatcher = Dispatchers.IO, api = apiMock)
    }

    @Test(expected = ApiErrorException::class)
    fun `given api has error, when ratesRepository getRates, then throw ApiErrorException`() =
        runTest {

            coEvery { apiMock.getRates("USD") } returns LatestRatesResponse(
                "error",
                "USD",
                emptyMap()
            )

            testedClass.getRates("USD")
        }

    @Test
    fun `given api has success, when ratesRepository getRates, then return rates`() =
        runTest {

            val expected = mapOf("USD" to 1.0f)
            coEvery { apiMock.getRates("USD") } returns LatestRatesResponse(
                "success",
                "USD",
                expected
            )
            Assert.assertEquals(expected, testedClass.getRates("USD"))

        }


}