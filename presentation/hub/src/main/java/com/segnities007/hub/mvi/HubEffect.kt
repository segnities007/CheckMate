package com.segnities007.hub.mvi

import com.segnities007.navigation.HubRoute
import com.segnities007.ui.mvi.MviEffect

sealed interface HubEffect : MviEffect {
    data class Navigate(
        val route: HubRoute,
    ) : HubEffect

    data class ShowToast(
        val message: String,
    ) : HubEffect

    object Logout : HubEffect
}
