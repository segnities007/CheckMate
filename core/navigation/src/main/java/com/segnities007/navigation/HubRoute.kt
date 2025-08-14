package com.segnities007.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface HubRoute : Route {
    @Serializable data object Home : HubRoute

    @Serializable data object Items : HubRoute

    @Serializable data object Dashboard : HubRoute

    @Serializable data object Templates : HubRoute

    @Serializable data object Setting : HubRoute
}
