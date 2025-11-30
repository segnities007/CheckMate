package com.segnities007.checkmate.mvi

import androidx.compose.runtime.Composable
import com.segnities007.model.UserStatus
import com.segnities007.navigation.NavKeys
import com.segnities007.ui.mvi.MviState

data class MainState(
    val userStatus: UserStatus = UserStatus(),
    val backStack: List<NavKeys> = listOf(NavKeys.SplashKey),
    val bottomBar: @Composable () -> Unit = {},
    val topBar: @Composable () -> Unit = {},
    val fab: @Composable () -> Unit = {},
) : MviState