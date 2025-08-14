package com.segnities007.login.mvi

import com.segnities007.ui.mvi.MviEffect

sealed interface LoginEffect : MviEffect {
    data object NavigateToHub : LoginEffect

    data class ShowToast(
        val message: String,
    ) : LoginEffect
}
