package com.segnities007.common.mvi

fun interface MviReducer<S : MviState, I : MviIntent> {
    fun reduce(currentState: S, intent: I): S
}