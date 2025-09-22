package com.segnities007.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.dashboard.DashboardScreen
import com.segnities007.home.HomeScreen
import com.segnities007.hub.mvi.HubEffect
import com.segnities007.hub.mvi.HubIntent
import com.segnities007.hub.mvi.HubState
import com.segnities007.hub.mvi.HubViewModel
import com.segnities007.items.ItemsScreen
import com.segnities007.navigation.HubRoute
import com.segnities007.navigation.Route
import com.segnities007.setting.SettingScreen
import com.segnities007.templates.TemplatesScreen
import org.koin.compose.koinInject
import androidx.compose.ui.graphics.Brush

@Composable
fun HubNavigation(onTopNavigate: (Route) -> Unit) {
    val hubNavController = rememberNavController()
    val hubViewModel: HubViewModel = koinInject()
    val state by hubViewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        hubViewModel.effect.collect { effect ->
            when (effect) {
                is HubEffect.Navigate -> {
                    hubNavController.navigate(effect.route)
                }
                is HubEffect.ShowToast -> {
                    // TODO
                }
                HubEffect.Logout -> {
                    onTopNavigate(Route.Auth)
                }
            }
        }
    }

    HubUi(state = state) { innerPadding ->
        NavHost(
            navController = hubNavController,
            startDestination = HubRoute.Home,
        ) {
            composable<HubRoute.Home> {
                HomeScreen(
                    innerPadding = innerPadding,
                    setFab = { hubViewModel.sendIntent(HubIntent.SetFab(it)) },
                    setTopBar = { hubViewModel.sendIntent(HubIntent.SetTopBar(it)) },
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
            composable<HubRoute.Items> {
                ItemsScreen(
                    innerPadding = innerPadding,
                    setFab = { hubViewModel.sendIntent(HubIntent.SetFab(it)) },
                    setTopBar = { hubViewModel.sendIntent(HubIntent.SetTopBar(it)) },
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
            composable<HubRoute.Dashboard> {
                DashboardScreen(
                    innerPadding = innerPadding,
                    setFab = { hubViewModel.sendIntent(HubIntent.SetFab(it)) },
                    setTopBar = { hubViewModel.sendIntent(HubIntent.SetTopBar(it)) },
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
            composable<HubRoute.Templates> {
                TemplatesScreen(
                    innerPadding = innerPadding,
                    setFab = { hubViewModel.sendIntent(HubIntent.SetFab(it)) },
                    setTopBar = { hubViewModel.sendIntent(HubIntent.SetTopBar(it)) },
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
            composable<HubRoute.Setting> {
                SettingScreen(
                    innerPadding = innerPadding,
                    setFab = { hubViewModel.sendIntent(HubIntent.SetFab(it)) },
                    setTopBar = { hubViewModel.sendIntent(HubIntent.SetTopBar(it)) },
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
        }
    }
}

@Composable
private fun HubUi(
    state: HubState,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
) {
    Scaffold(
        bottomBar = state.bottomBar,
        topBar = state.topBar,
        floatingActionButton = state.fab,
    ) { innerPadding ->
        content(innerPadding)
    }
}
