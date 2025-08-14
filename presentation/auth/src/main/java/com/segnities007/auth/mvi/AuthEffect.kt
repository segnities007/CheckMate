package com.segnities007.auth.mvi

import com.segnities007.navigation.AuthRoute
import com.segnities007.navigation.Route
import com.segnities007.ui.mvi.MviEffect

sealed interface AuthEffect : MviEffect {
    data class Navigate(
        val authRoute: AuthRoute,
    ) : AuthEffect

    data class TopNavigate(
        val route: Route,
    ) : AuthEffect

    data class ShowToast(
        val message: String,
    ) : AuthEffect
}
