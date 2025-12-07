package com.segnities007.navigation.mvi

import com.segnities007.ui.mvi.MviIntent

sealed interface MainIntent : MviIntent {

    data object CheckAccount : MainIntent

    data class ShowToast(
        val message: String,
    ) : MainIntent

    object Logout : MainIntent

    data object LoadUserStatus : MainIntent
}