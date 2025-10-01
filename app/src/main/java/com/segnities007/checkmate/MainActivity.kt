package com.segnities007.checkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.auth.AuthNavigation
import com.segnities007.checkmate.mvi.MainEffect
import com.segnities007.checkmate.mvi.MainIntent
import com.segnities007.checkmate.mvi.MainViewModel
import com.segnities007.ui.theme.CheckMateTheme
import com.segnities007.hub.HubNavigation
import com.segnities007.navigation.Route
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckMateTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
private fun MainNavigation() {
    val mainNavController = rememberNavController()
    val mainViewModel: MainViewModel = koinInject()

    val onNavigate: (Route) -> Unit = { route ->
        mainViewModel.sendIntent(MainIntent.Navigate(route))
    }

    LaunchedEffect(Unit) {
        mainViewModel.effect.collect {
            when (it) {
                is MainEffect.Navigate -> mainNavController.navigate(it.route)
            }
        }
    }

    NavHost(
        navController = mainNavController,
        startDestination = Route.Auth,
    ) {
        composable<Route.Auth> {
            AuthNavigation(onNavigate)
        }
        composable<Route.Hub> {
            HubNavigation(onNavigate)
        }
    }
}
