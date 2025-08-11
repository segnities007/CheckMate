package com.segnities007.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route{
    @Serializable object Auth : Route
    @Serializable object Hub : Route
}