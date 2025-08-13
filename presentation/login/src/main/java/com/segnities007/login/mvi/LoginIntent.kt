package com.segnities007.login.mvi

import com.segnities007.ui.mvi.MviIntent

internal sealed interface LoginIntent: MviIntent {
    data object ContinueWithGoogle: LoginIntent
    data object ContinueWithNothing: LoginIntent
}