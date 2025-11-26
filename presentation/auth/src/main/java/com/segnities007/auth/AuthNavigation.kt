package com.segnities007.auth

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.segnities007.auth.mvi.AuthEffect
import com.segnities007.auth.mvi.AuthViewModel
import com.segnities007.login.loginEntry
import com.segnities007.navigation.NavKey
import com.segnities007.navigation.Route
import com.segnities007.splash.splashEntry
import org.koin.compose.koinInject

@Composable
fun AuthNavigation(topNavigate: (Route) -> Unit) {
    val authViewModel: AuthViewModel = koinInject()
    var currentRoute by remember { mutableStateOf<NavKey>(NavKey.Splash) }

    val entryProvider = remember {
        entryProvider {
            splashEntry()
            loginEntry(topNavigate = topNavigate)
        }
    }

    NavDisplay(
        backStack = listOf(currentRoute),
        entryProvider = entryProvider,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )

    LaunchedEffect(Unit) {
        authViewModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.Navigate -> {
                    currentRoute = effect.authRoute
                }

                is AuthEffect.TopNavigate -> {
                    topNavigate(effect.route)
                }

                is AuthEffect.ShowToast -> {
                    // TODO
                }
            }
        }
    }
}
