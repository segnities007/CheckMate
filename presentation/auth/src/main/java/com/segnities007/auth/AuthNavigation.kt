package com.segnities007.auth

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.login.LoginScreen
import com.segnities007.navigation.AuthRoute
import com.segnities007.navigation.Route
import com.segnities007.splash.SplashScreen

@Composable
fun AuthNavigation(
    onNavigate: (Route) -> Unit
){
    val authNavController = rememberNavController()

    AuthUi {
        NavHost(
            navController = authNavController,
            startDestination = AuthRoute.Splash
        ){
            composable<AuthRoute.Splash>{
                SplashScreen()
            }
            composable<AuthRoute.Login>{
                LoginScreen()
            }
        }
    }
}

@Composable
private fun AuthUi(
    content: @Composable () -> Unit,
){
    Scaffold(

    ) { it
        content()
    }
}