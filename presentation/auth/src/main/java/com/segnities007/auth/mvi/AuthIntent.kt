package com.segnities007.auth.mvi

import com.segnities007.navigation.NavKey
import com.segnities007.ui.mvi.MviIntent

sealed interface AuthIntent : MviIntent {
    data class Navigate(
        val authRoute: NavKey,
    ) : AuthIntent

    data class TopNavigate(
        val route: NavKey,
    ) : AuthIntent

    data object CheckAccount : AuthIntent

    data class ShowToast(
        val message: String,
    ) : AuthIntent
}
