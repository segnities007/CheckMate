package com.segnities007.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<I : MviIntent, S : MviState, E : MviEffect>(
    initialState: S
) : ViewModel() {

    private val stateMachine = StateMachine<S, E>(initialState, viewModelScope)

    val uiState: StateFlow<UiState<S>> = stateMachine.uiState
    val effect = stateMachine.effect

    protected val currentState: S
        get() = uiState.value.data

    abstract suspend fun handleIntent(intent: I)

    fun sendIntent(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    protected fun sendEffect(builder: () -> E) {
        stateMachine.sendEffect(builder)
    }

    protected fun <T> execute(
        action: suspend () -> T,
        reducer: S.(T) -> S
    ) {
        stateMachine.execute(action, reducer)
    }

    protected fun setState(reducer: S.() -> S) {
        stateMachine.setState(reducer)
    }
}