package com.segnities007.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.segnities007.dashboard.component.ProgressCardsRow
import com.segnities007.dashboard.component.StatCardsRow
import com.segnities007.dashboard.component.UncheckedItemsSection
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

    val targetAlpha by remember {
        derivedStateOf {
            when {
                scrollState.value > 50 -> 0f // 50px以上スクロールしたら非表示
                else -> (1f - scrollState.value / 50f).coerceIn(0f, 1f) // 0-50pxの範囲でフェード
            }
        }
    }

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 200),
        label = "navigationBarAlpha",
    )

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
                .background(MaterialTheme.colorScheme.surfaceVariant)
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
    ) {
        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HorizontalDividerWithLabel("統計")
            StatCardsRow(
                itemCount = state.itemCount,
                templateCount = state.templateCount,
            )
            ProgressCardsRow(
                checkedItemCountToday = state.checkedItemCountToday,
                scheduledItemCountToday = state.scheduledItemCountToday,
                totalCheckedRecordsCount = state.totalCheckedRecordsCount,
                totalRecordsCount = state.totalRecordsCount,
            )
            UncheckedItemsSection(
                uncheckedItemsToday = state.uncheckedItemsToday,
                uncheckedItemsTomorrow = state.uncheckedItemsTomorrow,
            )
        }
    }
}
