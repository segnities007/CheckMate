package com.segnities007.ui.mvi

interface MviState

/**
 * UIの読み込み状態を表すステートマシン。
 * Loading, Success, Failure, Idle の4つの状態を持つ。
 * 常にデータを保持し、状態遷移時もデータを引き継ぐ。
 */
sealed interface UiState<T : MviState> {
    val data: T

    /**
     * 初期状態。
     */
    data class Idle<T : MviState>(override val data: T) : UiState<T>

    /**
     * 読み込み中。以前のデータを保持する。
     */
    data class Loading<T : MviState>(override val data: T) : UiState<T>

    /**
     * 成功。新しいデータを持つ。
     */
    data class Success<T : MviState>(override val data: T) : UiState<T>

    /**
     * 失敗。エラーメッセージを持ち、以前のデータを保持する。
     */
    data class Failure<T : MviState>(val message: String, override val data: T) : UiState<T>

    fun toLoading(): UiState<T> = Loading(data)
    fun toFailure(message: String): UiState<T> = Failure(message, data)
    fun toSuccess(newData: T): UiState<T> = Success(newData)
    fun toIdle(): UiState<T> = Idle(data)
    fun toIdle(newData: T): UiState<T> = Idle(newData)
}