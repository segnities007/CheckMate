package com.segnities007.hub

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.dashboard.DashboardScreen
import com.segnities007.home.HomeScreen
import com.segnities007.items.ItemsScreen
import com.segnities007.navigation.HubRoute
import com.segnities007.navigation.Route
import com.segnities007.setting.SettingScreen
import com.segnities007.templates.TemplatesScreen

@Composable
fun HubNavigation(
    onTopNavigate: (Route) -> Unit
) {
    val hubNavController = rememberNavController()

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
