package com.segnities007.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface HubRoute : Route {
    @Serializable object Home : HubRoute

    @Serializable object Items : HubRoute

    @Serializable object Dashboard : HubRoute

    @Serializable object Templates : HubRoute

    @Serializable object Setting : HubRoute
}
