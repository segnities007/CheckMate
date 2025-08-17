package com.segnities007.hub.mvi

import com.segnities007.navigation.HubRoute
import com.segnities007.ui.mvi.MviIntent

sealed interface HubIntent : MviIntent {
    data class Navigate(
        val hubRoute: HubRoute,
    ) : HubIntent

    data class ShowToast(
        val message: String,
    ) : HubIntent

    data object ShowNavigationBar : HubIntent

    data object HideNavigationBar : HubIntent

    object Logout : HubIntent
}
