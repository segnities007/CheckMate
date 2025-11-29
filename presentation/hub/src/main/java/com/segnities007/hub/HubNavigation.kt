package com.segnities007.hub

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.segnities007.dashboard.dashboardEntry
import com.segnities007.home.homeEntry
import com.segnities007.hub.mvi.HubEffect
import com.segnities007.hub.mvi.HubIntent
import com.segnities007.hub.mvi.HubViewModel
import com.segnities007.items.itemsEntry
import com.segnities007.navigation.NavKey
import com.segnities007.setting.settingEntry
import com.segnities007.templates.templatesEntry
import org.koin.compose.koinInject

@Composable
fun HubNavigation(onTopNavigate: (NavKey) -> Unit) {
    val hubViewModel: HubViewModel = koinInject()
    val uiState by hubViewModel.uiState.collectAsState()
    val state = uiState.data

    LaunchedEffect(Unit) {
        hubViewModel.effect.collect { effect ->
            when (effect) {
                is HubEffect.ShowToast -> {
                    // TODO
                }

                HubEffect.Logout -> {
                    onTopNavigate(NavKey.Auth)
                }
            }
        }
    }

    val entryProvider = remember {
        entryProvider {
            homeEntry(
                onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) }
            )
            itemsEntry(
                onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) }
            )
            dashboardEntry(
                onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) }
            )
            templatesEntry(
                onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) }
            )
            settingEntry(
                onNavigate = { hubViewModel.sendIntent(HubIntent.Navigate(it)) }
            )
        }
    }

    NavDisplay(
        backStack = listOf(state.currentHubRoute),
        entryProvider = entryProvider,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}
