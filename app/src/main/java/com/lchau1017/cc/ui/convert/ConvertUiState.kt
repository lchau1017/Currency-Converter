package com.lchau1017.cc.ui.convert

sealed interface UiState {
    data class InitData(val fromValue: String, val toValue: String) : UiState
    data class CountDown(val seconds: Int) : UiState
}


sealed interface Effect {
    object BackToBegin : Effect
    data class ShowPopup(val fromValue: String, val toValue: String) : Effect
    data class ShowResult(val amount: String, val rate: String) : Effect
    data class ShowError(val error: String) : Effect
}
