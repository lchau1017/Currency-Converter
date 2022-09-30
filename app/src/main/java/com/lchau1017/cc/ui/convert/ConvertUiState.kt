package com.lchau1017.cc.ui.convert

data class UiStateData(val fromValue: String, val toValue: String,val seconds: Int = 0)



sealed interface Effect {
    object BackToBegin : Effect
    data class ShowPopup(val fromValue: String, val toValue: String) : Effect
    data class ShowResult(val amount: String, val rate: String) : Effect
    data class ShowError(val error: String) : Effect
}
