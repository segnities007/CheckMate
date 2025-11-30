package com.segnities007.splash.mvi

import com.segnities007.ui.mvi.MviIntent

sealed interface SplashIntent : MviIntent {
    data object CheckAccount : SplashIntent
}
