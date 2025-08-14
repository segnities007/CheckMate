package com.segnities007.ui.mvi

fun interface MviReducer<S : MviState, I : MviIntent> {
    fun reduce(
        currentState: S,
        intent: I,
    ): S
}
