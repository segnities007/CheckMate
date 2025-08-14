package com.segnities007.hub

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.dashboard.DashboardScreen
import com.segnities007.home.HomeScreen
import com.segnities007.hub.mvi.HubViewModel
import com.segnities007.items.ItemsScreen
import com.segnities007.navigation.HubRoute
import com.segnities007.navigation.Route
import com.segnities007.setting.SettingScreen
import com.segnities007.templates.TemplatesScreen
import org.koin.compose.koinInject

@Composable
fun HubNavigation(
    onTopNavigate: (Route) -> Unit
) {
    val hubNavController = rememberNavController()
    val hubViewModel: HubViewModel = koinInject()
    val state by hubViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        hubViewModel.effect.collect { effect ->
            when (effect) {
                is com.segnities007.hub.mvi.HubEffect.Navigate -> {
                    hubNavController.navigate(effect.route)
                }
                is com.segnities007.hub.mvi.HubEffect.ShowToast -> {
                    // TODO: show toast
                }
                com.segnities007.hub.mvi.HubEffect.Logout -> {
                    onTopNavigate(Route.Auth)
                }
            }
        }
    }

    val onNavigate: (HubRoute) -> Unit = { route ->
        hubViewModel.sendIntent(com.segnities007.hub.mvi.HubIntent.Navigate(route))
    }

    HubUi {
        NavHost(
            navController = hubNavController,
            startDestination = HubRoute.Home,
        ) {
            composable<HubRoute.Home> {
                HomeScreen()
            }
            composable<HubRoute.Items> {
                ItemsScreen()
            }
            composable<HubRoute.Dashboard> {
                DashboardScreen()
            }
            composable<HubRoute.Templates> {
                TemplatesScreen()
            }
            composable<HubRoute.Setting> {
                SettingScreen()
            }
        }
    }
}

@Composable
private fun HubUi(content: @Composable () -> Unit) {
    Scaffold {
        it
        content()
    }
}
