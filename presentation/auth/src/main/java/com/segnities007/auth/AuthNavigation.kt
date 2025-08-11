package com.segnities007.auth

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.segnities007.navigation.AuthRoute

@Composable
fun AuthNavigation(
    onNavigate: (AuthRoute) -> Unit
){
    val authNavController = rememberNavController()

    AuthUi {
        NavHost(
            navController = authNavController,
            startDestination = AuthRoute.Splash
        ){
            //TODO
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