package com.segnities007.splash.mvi

import com.segnities007.common.keys.NavKeys
import com.segnities007.ui.mvi.MviEffect

sealed interface SplashEffect : MviEffect {
    data class NavigateTo(val route: NavKeys) : SplashEffect
}
