package com.segnities007.hub

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.navigation.HubRoute
import com.segnities007.navigation.Route

@Composable
fun HubNavigation(
    onNavigate: (Route) -> Unit,
){
    val hubNavController = rememberNavController()

    HubUi{
        NavHost(
            navController = hubNavController,
            startDestination = HubRoute.Home,
        ){
            composable<HubRoute.Home>{
                //TODO
            }
            composable<HubRoute.Items>{
                //TODO
            }
            composable<HubRoute.Dashboard>{
                //TODO
            }
            composable<HubRoute.Templates>{
                //TODO
            }
            composable<HubRoute.Setting>{
                //TODO
            }
        }
    }
}

@Composable
private fun HubUi(
    content: @Composable () -> Unit,
){
    Scaffold{ it
        content()
    }
}