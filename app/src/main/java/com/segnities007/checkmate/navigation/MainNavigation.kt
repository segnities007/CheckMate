package com.segnities007.checkmate.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.segnities007.checkmate.mvi.MainEffect
import com.segnities007.checkmate.mvi.MainViewModel
import com.segnities007.dashboard.dashboardEntry
import com.segnities007.home.homeEntry
import com.segnities007.items.itemsEntry
import com.segnities007.login.loginEntry
import com.segnities007.navigation.NavKeys
import com.segnities007.setting.settingEntry
import com.segnities007.splash.splashEntry
import com.segnities007.templates.templatesEntry
import org.koin.compose.koinInject

private const val MIN_BACK_STACK_SIZE = 1

@Composable
internal fun MainNavigation() {
    val mainViewModel: MainViewModel = koinInject()
    val backStack = rememberNavBackStack(NavKeys.SplashKey)
    val onNavigate: (NavKeys) -> Unit = { backStack.add(it) }
    val onBack: () -> Unit = { backStack.removeLastOrNull() }

    LaunchedEffect(Unit) {
        mainViewModel.effect.collect { effect ->
            when (effect) {
                is MainEffect.Navigate -> {
                    with(backStack) {
                        if (effect.route is NavKeys.Hub || effect.route is NavKeys.Auth) clear()
                        add(effect.route)
                    }
                }

                is MainEffect.ShowToast -> {
                    // TODO: Show toast
                }

                MainEffect.Logout -> {
                    with(backStack) {
                        clear()
                        add(NavKeys.Auth.LoginKey)
                    }
                }
            }
        }
    }

    BackHandler(enabled = (backStack.size <= MIN_BACK_STACK_SIZE)) {
        // 何もしない（アプリ終了を防ぐ）
    }

    val entryProvider = entryProvider {
        splashEntry(
            onNavigate = {
                with(backStack) {
                    clear()
                    add(it)
                }
            }
        )
        loginEntry(onNavigate = onNavigate)
        homeEntry(onNavigate = onNavigate, onBack = onBack)
        itemsEntry(onNavigate = onNavigate, onBack = onBack)
        templatesEntry(onNavigate = onNavigate, onBack = onBack)
        dashboardEntry(onNavigate = onNavigate, onBack = onBack)
        settingEntry(onNavigate = onNavigate, onBack = onBack)
    }

    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider,
    )
}