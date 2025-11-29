package com.segnities007.ui.mvi

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVIアーキテクチャにおける状態管理と副作用の送信を担当するクラス。
 * ViewModelからロジックを分離し、純粋なステートマシンとして機能する。
 */
class StateMachine<S : MviState, E : MviEffect>(
    initialState: S,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow<UiState<S>>(UiState.Idle(initialState))
    val uiState: StateFlow<UiState<S>> = _uiState.asStateFlow()

    private val _effect = Channel<E>()
    val effect = _effect.receiveAsFlow()

    /**
     * 【非同期】API通信など
     * Loading -> 処理 -> Success/Failure を自動で行う
     */
    fun <T> execute(
        action: suspend () -> T,
        reducer: S.(T) -> S,
    ) {
        scope.launch {
            _uiState.update { it.toLoading() }

            try {
                val result = action()
                _uiState.update { state ->
                    val currentData = state.data
                    val newData = currentData.reducer(result)
                    state.toSuccess(newData)
                }
            } catch (e: CancellationException) {
                
            } catch (e: Exception) {
                _uiState.update { it.toFailure(e.message ?: "Error") }
            }
        }
    }

    /**
     * 【同期】入力フォーム、トグルなど
     * Loadingを出さずに即座にデータを書き換える際に使用
     */
    fun setState(reducer: S.() -> S) {
        _uiState.update { state ->
            val currentData = state.data
            val newData = currentData.reducer()
            state.toSuccess(newData)
        }
    }

    /**
     * 副作用（Effect）を送信する
     */
    fun sendEffect(builder: () -> E) {
        scope.launch {
            _effect.send(builder())
        }
    }

    fun sendEffect(effect: E) {
        scope.launch {
            _effect.send(effect)
        }
    }
}
