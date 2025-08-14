package com.segnities007.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthRoute : Route {
    @Serializable object Login : AuthRoute

    @Serializable object Splash : AuthRoute
}
