package com.segnities007.splash

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.segnities007.navigation.NavKeys
import com.segnities007.splash.mvi.SplashEffect
import com.segnities007.splash.mvi.SplashViewModel
import org.koin.compose.koinInject

fun EntryProviderScope<NavKey>.splashEntry(
    onNavigate: (NavKeys) -> Unit,
) {
    entry<NavKeys.SplashKey> {
        val viewModel: SplashViewModel = koinInject()

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is SplashEffect.NavigateTo -> onNavigate(effect.route)
                }
            }
        }

        SplashScreen()
    }
}
