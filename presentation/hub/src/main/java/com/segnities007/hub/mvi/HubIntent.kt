package com.segnities007.hub.mvi

import androidx.compose.runtime.Composable
import com.segnities007.navigation.NavKey
import com.segnities007.ui.mvi.MviIntent

sealed interface HubIntent : MviIntent {
    data class Navigate(
        val hubRoute: NavKey,
    ) : HubIntent

    data class ShowToast(
        val message: String,
    ) : HubIntent

    object Logout : HubIntent

    data class SetBottomBar(
        val bottomBar: @Composable () -> Unit,
    ) : HubIntent

    data class SetTopBar(
        val topBar: @Composable () -> Unit,
    ) : HubIntent

    data class SetFab(
        val fab: @Composable () -> Unit,
    ) : HubIntent

    data object LoadUserStatus : HubIntent
}
