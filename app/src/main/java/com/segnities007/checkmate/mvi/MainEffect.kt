package com.segnities007.checkmate.mvi

import com.segnities007.navigation.NavKey
import com.segnities007.ui.mvi.MviEffect

internal sealed interface MainEffect : MviEffect {
    data class Navigate(
        val route: NavKey,
    ) : MainEffect
}
