package com.lchau1017.cc.ui.calculate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lchau1017.cc.domain.usecase.GetRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CalculateAmountViewModel @Inject constructor(
    private val getRatesUseCase: GetRatesUseCase,
    @Named("default") private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _mutableState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Loading)
    val state: StateFlow<UiState> = _mutableState.asStateFlow()

    private var _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    fun getRatesLabels() {
        viewModelScope.launch {
            getRatesUseCase.getRatesLabels()
                .onStart {
                    _mutableState.value = UiState.Loading
                }
                .flowOn(dispatcher)
                .collect { result ->
                    result.onSuccess {
                        _mutableState.value =
                            UiState.InitData(it.first, it.second)
                    }.onFailure { failure ->
                        failure.message?.let {
                            _effect.emit(Effect.ShowError(it))
                        }
                    }
                }
        }
    }

    fun calculate(amount: String, base: String, to: String) {
        viewModelScope.launch {
            getRatesUseCase.convert(amount, base, to)
                .onStart {
                    _mutableState.value = UiState.Loading
                }
                .collect { result ->
                    result.onSuccess {
                        _effect.emit(Effect.ToConfirmScreen(it.fromValue, it.toValue, it.rate))
                    }.onFailure { failure ->
                        failure.message?.let {
                            _effect.emit(Effect.ShowError(it))
                        }
                    }
                }
        }
    }

}
