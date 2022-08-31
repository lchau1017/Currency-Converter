package com.lchau1017.cc

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> StateFlow<T>.collectForTesting(testBlock: suspend FlowTurbine<T>.() -> Unit) {
    runTest {
        test {
            testBlock()
        }
    }
}

fun <T> SharedFlow<T>.collectForTesting(testBlock: suspend FlowTurbine<T>.() -> Unit) {
    runTest {
        test {
            testBlock()
            cancelAndConsumeRemainingEvents()
        }
    }
}