package com.enriqueajin.pomidorki.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S, UiEvent, InternalEvent, F>(
    initialState: S,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEffects = Channel<F>(Channel.BUFFERED)
    val uiEffects: Flow<F> = _uiEffects.receiveAsFlow()

    open fun onEvent(event: UiEvent) = handleUiEvent(event)

    protected fun dispatch(event: InternalEvent) = handleInternalEvent(event)

    protected abstract fun handleUiEvent(event: UiEvent)

    protected abstract fun handleInternalEvent(event: InternalEvent)

    protected fun setState(reducer: S.() -> S) {
        _uiState.update { it.reducer() }
    }

    protected fun emitEffect(effect: F) {
        _uiEffects.trySend(effect)
    }
}
