package com.segnities007.hub.mvi

import androidx.compose.runtime.Composable
import com.segnities007.model.UserStatus
import com.segnities007.navigation.HubRoute
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.mvi.MviState

data class HubState(
    val userStatus: UserStatus = UserStatus(),
    val currentHubRoute: HubRoute = HubRoute.Home,
    val bottomBar: @Composable () -> Unit = {},
    val topBar: @Composable () -> Unit = {},
    val fab: @Composable () -> Unit = {},
) : MviState
