package com.segnities007.auth

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.auth.mvi.AuthViewModel
import com.segnities007.login.LoginScreen
import com.segnities007.navigation.AuthRoute
import com.segnities007.navigation.Route
import com.segnities007.splash.SplashScreen
import org.koin.compose.koinInject

@Composable
fun AuthNavigation(
    topNavigate: (Route) -> Unit,
) {
    val authNavController = rememberNavController()
    val authViewModel: AuthViewModel = koinInject()

    LaunchedEffect(Unit) {
        authViewModel.effect.collect { effect ->
            when (effect) {
                is com.segnities007.auth.mvi.AuthEffect.Navigate -> {
                    authNavController.navigate(effect.authRoute)
                }

                is com.segnities007.auth.mvi.AuthEffect.TopNavigate -> {
                    topNavigate(effect.route)
                }

                is com.segnities007.auth.mvi.AuthEffect.ShowToast -> {
                    // TODO
                }
            }
        }
    }

    AuthUi {
        NavHost(
            navController = authNavController,
            startDestination = AuthRoute.Splash,
        ) {
            composable<AuthRoute.Splash> {
                SplashScreen()
            }
            composable<AuthRoute.Login> {
                LoginScreen(
                    topNavigate = topNavigate,
                )
            }
        }
    }
}

@Composable
private fun AuthUi(content: @Composable () -> Unit) {
    Scaffold {
        it
        content()
    }
}
