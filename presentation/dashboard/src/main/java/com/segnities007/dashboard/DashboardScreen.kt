package com.segnities007.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.dashboard.component.StatCard
import com.segnities007.dashboard.component.UncheckedItemsCard
import com.segnities007.dashboard.mvi.DashboardState
import com.segnities007.dashboard.mvi.DashboardViewModel
import com.segnities007.navigation.HubRoute
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    innerPadding: PaddingValues,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val dashboardViewModel: DashboardViewModel = koinInject()
    val state by dashboardViewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    val alpha by remember {
        derivedStateOf {
            (1f - scrollState.value / 100f).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(Unit) {
        setFab {}
        setTopBar {}
        setNavigationBar {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = HubRoute.Dashboard,
                onNavigate = onNavigate,
            )
        }
    }

    DashboardUi(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        state = state,
        innerPadding = innerPadding,
    )
}

@Composable
private fun DashboardUi(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    state: DashboardState,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
        HorizontalDividerWithLabel("統計")
        StatCard(
            title = "総アイテム数",
            value = state.itemCount.toString(),
            icon = Icons.Filled.Dns,
            iconTint = MaterialTheme.colorScheme.primary,
        )
        StatCard(
            title = "総テンプレート数",
            value = state.templateCount.toString(),
            icon = Icons.AutoMirrored.Filled.Assignment,
            iconTint = MaterialTheme.colorScheme.secondary,
        )
        UncheckedItemsCard(
            title = "本日の未チェックアイテム",
            items = state.uncheckedItemsToday,
            icon = Icons.AutoMirrored.Filled.ListAlt,
            iconTint = MaterialTheme.colorScheme.tertiary,
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}



