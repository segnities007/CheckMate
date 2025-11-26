package com.segnities007.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.segnities007.ui.card.statistics.StatCard
import com.segnities007.ui.card.statistics.StatCardWithPercentage
import com.segnities007.ui.card.UncheckedItemsCard
import com.segnities007.dashboard.mvi.DashboardState
import com.segnities007.dashboard.mvi.DashboardViewModel
import com.segnities007.navigation.NavKey
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import com.segnities007.ui.util.rememberScrollVisibility
import org.koin.compose.koinInject

import com.segnities007.ui.scaffold.CheckMateScaffold

import com.segnities007.ui.theme.checkMateBackgroundBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (NavKey) -> Unit,
) {
    val dashboardViewModel: DashboardViewModel = koinInject()
    val state by dashboardViewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val isVisible by rememberScrollVisibility(scrollState = scrollState)

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "navigationBarAlpha",
    )

    CheckMateScaffold(
        bottomBar = {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = NavKey.Dashboard,
                onNavigate = onNavigate,
            )
        }
    ) { innerPadding ->
        DashboardUi(
            state = state,
            scrollState = scrollState,
            innerPadding = innerPadding,
        )
    }
}

@Composable
private fun DashboardUi(
    innerPadding: PaddingValues,
    state: DashboardState,
    scrollState: ScrollState,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.checkMateBackgroundBrush)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HorizontalDividerWithLabel("統計")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    title = "総アイテム数",
                    value = state.itemCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    title = "総テンプレート数",
                    value = state.templateCount.toString(),
                    modifier = Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCardWithPercentage(
                    title = "本日の完了率",
                    value = "${state.checkedItemCountToday}/${state.scheduledItemCountToday})",
                    progress =
                        if (state.scheduledItemCountToday >
                            0
                        ) {
                            (state.checkedItemCountToday.toFloat() / state.scheduledItemCountToday)
                        } else {
                            0f
                        },
                    modifier = Modifier.weight(1f),
                )
                StatCardWithPercentage(
                    title = "累計完了率",
                    value = "${state.totalCheckedRecordsCount}/${state.totalRecordsCount}",
                    progress =
                        if (state.totalRecordsCount >
                            0
                        ) {
                            (state.totalCheckedRecordsCount.toFloat() / state.totalRecordsCount)
                        } else {
                            0f
                        },
                    modifier = Modifier.weight(1f),
                )
            }
            UncheckedItemsCard(
                title = "本日の未チェックアイテム",
                items = state.uncheckedItemsToday,
            )
            UncheckedItemsCard(
                title = "明日の未チェックアイテム",
                items = state.uncheckedItemsTomorrow,
            )
        }
    }
}

@Preview
@Composable
fun DashboardUiPreview() {
    DashboardUi(
        innerPadding = PaddingValues(0.dp),
        state = DashboardState(
            itemCount = 10,
            templateCount = 5,
            checkedItemCountToday = 3,
            scheduledItemCountToday = 5,
            totalCheckedRecordsCount = 20,
            totalRecordsCount = 30,
            uncheckedItemsToday = listOf()
        ),
        scrollState = rememberScrollState(),
    )
}




