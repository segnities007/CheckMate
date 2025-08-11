package com.segnities007.checkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.checkmate.ui.theme.CheckMateTheme
import com.segnities007.navigation.Route

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
private fun MainNavigation(){
    val mainNavController = rememberNavController()

    NavHost(
        navController = mainNavController,
        startDestination = Route.Auth,
    ){
        composable<Route.Auth>{
            //TODO
        }
        composable<Route.Hub>{
            //TODO
        }
    }
}