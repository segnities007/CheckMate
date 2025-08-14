package com.segnities007.hub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.dashboard.DashboardScreen
import com.segnities007.home.HomeScreen
import com.segnities007.hub.mvi.HubIntent
import com.segnities007.hub.mvi.HubViewModel
import com.segnities007.items.ItemsScreen
import com.segnities007.navigation.HubRoute
import com.segnities007.navigation.Route
import com.segnities007.setting.SettingScreen
import com.segnities007.templates.TemplatesScreen
import com.segnities007.ui.bar.FloatingNavigationBar
import org.koin.compose.koinInject

@Composable
fun HubNavigation(
    onTopNavigate: (Route) -> Unit
) {
    val hubNavController = rememberNavController()
    val hubViewModel: HubViewModel = koinInject()
    val state by hubViewModel.state.collectAsState()
    var currentRoute by remember { mutableStateOf<HubRoute>(HubRoute.Home) }
    val updateCurrentRoute: (HubRoute) -> Unit = { currentRoute = it }

    LaunchedEffect(Unit) {
        hubViewModel.effect.collect { effect ->
            when (effect) {
                is com.segnities007.hub.mvi.HubEffect.Navigate -> {
                    currentRoute = effect.route
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

    HubUi(
        currentRoute = currentRoute,
        updateCurrentRoute = updateCurrentRoute,
        sendIntent = hubViewModel::sendIntent
    ){
        NavHost(
            navController = hubNavController,
            startDestination = HubRoute.Home,
        ) {
            composable<HubRoute.Home> {
                HomeScreen(
                    currentRoute = currentRoute,
                )
            }
            composable<HubRoute.Items> {
                ItemsScreen(
                    currentRoute = currentRoute,
                )
            }
            composable<HubRoute.Dashboard> {
                DashboardScreen(
                    currentRoute = currentRoute,
                )
            }
            composable<HubRoute.Templates> {
                TemplatesScreen(
                    currentRoute = currentRoute,
                )
            }
            composable<HubRoute.Setting> {
                SettingScreen(
                    userStatus = state.userStatus,
                )
            }
        }
    }
}

@Composable
private fun HubUi(
    currentRoute: HubRoute = HubRoute.Home,
    updateCurrentRoute: (HubRoute) -> Unit,
    sendIntent: (HubIntent) -> Unit,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val alpha by remember {
        derivedStateOf {
            (1f - scrollState.value / 300f).coerceIn(0.2f, 1f)
        }
    }

    val bottomBar: @Composable () -> Unit = {
        FloatingNavigationBar(
            alpha = alpha,
            currentHubRoute = currentRoute,
            onNavigate = {
                updateCurrentRoute(it)
                sendIntent(HubIntent.Navigate(it))
            }
        )
    }

    Scaffold(
        bottomBar = bottomBar,
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ){
            Spacer(modifier = Modifier.padding(top = it.calculateTopPadding()))
            content()
            Spacer(modifier = Modifier.padding(bottom = it.calculateBottomPadding()))
        }
    }
}
