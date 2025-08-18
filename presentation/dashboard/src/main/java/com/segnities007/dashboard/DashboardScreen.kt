package com.segnities007.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.segnities007.navigation.HubRoute
import com.segnities007.ui.bar.FloatingNavigationBar

@Composable
fun DashboardScreen(
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val scrollState = rememberScrollState()

    val alpha by remember {
        derivedStateOf {
            (1f - scrollState.value / 100f).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(Unit) {
        setNavigationBar {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = HubRoute.Dashboard,
                onNavigate = onNavigate,
            )
        }
    }

    Column(
        modifier = Modifier.verticalScroll(scrollState),
    ) {
        DashboardUi()
    }
}

@Composable
private fun DashboardUi() {
}
