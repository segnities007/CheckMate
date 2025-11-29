package com.segnities007.auth.mvi

import com.segnities007.navigation.NavKey

import com.segnities007.ui.mvi.MviEffect

sealed interface AuthEffect : MviEffect {


    data class TopNavigate(
        val route: NavKey,
    ) : AuthEffect

    data class ShowToast(
        val message: String,
    ) : AuthEffect
}
