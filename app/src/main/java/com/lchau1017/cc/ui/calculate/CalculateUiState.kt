package com.lchau1017.cc.ui.calculate

sealed interface UiState {
    data class InitData(val fromLabels: List<String>, val toLabels: List<String>) : UiState
    object Loading : UiState
}


sealed interface Effect {
    data class ToConfirmScreen(val fromValue: String, val toValue: String, val rate: String) :
        Effect

    data class ShowError(val error: String) : Effect
}
