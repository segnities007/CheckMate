package com.segnities007.ui.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StateMachine<S : MviState, E : MviEffect>(
    initialState: S,
    private val scope: CoroutineScope
) {
    private val _uiState = MutableStateFlow<UiState<S>>(UiState.Idle(initialState))
    val uiState: StateFlow<UiState<S>> = _uiState.asStateFlow()

    private val _effect = Channel<E>()
    val effect: Flow<E> = _effect.receiveAsFlow()

    fun sendEffect(builder: () -> E) {
        scope.launch {
            _effect.send(builder())
        }
    }

    fun setState(reducer: S.() -> S) {
        _uiState.update { currentState ->
            val newState = currentState.data.reducer()
            currentState.toIdle(newState)
        }
    }

    fun <T> execute(
        action: suspend () -> T,
        reducer: S.(T) -> S
    ) {
        scope.launch {
            _uiState.update { it.toLoading() }
            try {
                val result = action()
                _uiState.update { currentState ->
                    val newState = currentState.data.reducer(result)
                    currentState.toSuccess(newState)
                }
            } catch (e: Exception) {
                _uiState.update { it.toFailure(e.message ?: "Unknown error") }
            }
        }
    }
}