package com.segnities007.checkmate.mvi

import androidx.compose.runtime.Composable
import com.segnities007.navigation.NavKeys
import com.segnities007.ui.mvi.MviIntent

sealed interface MainIntent : MviIntent {
    data class Navigate(
        val route: NavKeys,
    ) : MainIntent

    data object GoBack : MainIntent

    data object CheckAccount : MainIntent

    data class ShowToast(
        val message: String,
    ) : MainIntent

    object Logout : MainIntent

    data class SetBottomBar(
        val bottomBar: @Composable () -> Unit,
    ) : MainIntent

    data class SetTopBar(
        val topBar: @Composable () -> Unit,
    ) : MainIntent

    data class SetFab(
        val fab: @Composable () -> Unit,
    ) : MainIntent

    data object LoadUserStatus : MainIntent
}