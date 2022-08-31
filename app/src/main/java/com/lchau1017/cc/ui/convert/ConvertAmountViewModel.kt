package com.lchau1017.cc.ui.convert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lchau1017.cc.domain.usecase.CountDownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ConvertAmountViewModel @Inject constructor(
    private val countDownUseCase: CountDownUseCase,
    @Named("default") private val dispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _mutableState: MutableStateFlow<UiState> =
        MutableStateFlow(
            UiState.InitData(
                savedStateHandle.get<String>("fromValue")!!,
                savedStateHandle.get<String>("toValue")!!
            )
        )

    val state: StateFlow<UiState> = _mutableState.asStateFlow()

    private var _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    var countDownJob: Job? = null

    fun countDown() {
        countDownJob = viewModelScope.launch {
            flow {
                val startingValue = countDownUseCase.getCountDownTime()
                emit(startingValue)
                var currentValue = startingValue
                while (currentValue > 0) {
                    delay(1000L)
                    currentValue--
                    emit(currentValue)
                }
            }
                .cancellable()
                .flowOn(dispatcher)
                .catch {
                    it.message?.let { message ->
                        _effect.emit(Effect.ShowError(message))
                    }
                }
                .distinctUntilChanged()
                .collect {
                    _mutableState.value = UiState.CountDown(it)
                    if (it == 0) {
                        _effect.emit(Effect.BackToBegin)
                    }
                }
        }
    }

    fun convert() {
        viewModelScope.launch {
            _effect.emit(
                Effect.ShowPopup(
                    savedStateHandle.get<String>("fromValue")!!,
                    savedStateHandle.get<String>("toValue")!!
                )
            )
        }
    }

    fun showResult() {
        viewModelScope.launch {
            countDownJob?.cancel()
            _effect.emit(
                Effect.ShowResult(
                    savedStateHandle.get<String>("toValue")!!,
                    savedStateHandle.get<String>("rate")!!
                )
            )
        }
    }

}
