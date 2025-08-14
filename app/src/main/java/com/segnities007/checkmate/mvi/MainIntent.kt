package com.segnities007.checkmate.mvi

import com.segnities007.navigation.Route
import com.segnities007.ui.mvi.MviIntent

internal sealed interface MainIntent : MviIntent {
    data class Navigate(
        val route: Route,
    ) : MainIntent
}
