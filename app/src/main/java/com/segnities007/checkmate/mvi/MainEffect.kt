package com.segnities007.checkmate.mvi

import com.segnities007.navigation.NavKeys
import com.segnities007.ui.mvi.MviEffect

sealed interface MainEffect : MviEffect {
    data class Navigate(
        val route: NavKeys,
    ) : MainEffect

    data class ShowToast(
        val message: String,
    ) : MainEffect

    object Logout : MainEffect
}