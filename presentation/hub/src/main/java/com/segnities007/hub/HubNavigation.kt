package com.segnities007.hub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.segnities007.ui.bar.FloatingNavigationBar
import org.koin.compose.koinInject

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
                    //TODO
                }
                HubEffect.Logout -> {
                    onTopNavigate(Route.Auth)
                }
            }
        }
    }

    HubUi(state = state) {
        NavHost(
            navController = hubNavController,
            startDestination = HubRoute.Home,
        ) {
            composable<HubRoute.Home> {
                HomeScreen(
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
            composable<HubRoute.Items> {
                ItemsScreen(
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
            composable<HubRoute.Dashboard> {
                DashboardScreen(
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
            composable<HubRoute.Templates> {
                TemplatesScreen(
                    setNavigationBar = { hubViewModel.sendIntent(HubIntent.SetBottomBar(it)) },
                    onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) },
                )
            }
            composable<HubRoute.Setting> {
                SettingScreen(
                    userStatus = state.userStatus,
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
    content: @Composable () -> Unit,
) {
    Scaffold(
        bottomBar = state.bottomBar,
        topBar = state.topBar,
        floatingActionButton = state.fab,
    ) { innerPadding ->
        Column{
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            content()
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        }
    }
}
