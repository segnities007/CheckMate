package com.segnities007.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.ui.card.StatCard
import com.segnities007.ui.card.StatCardWithPercentage
import com.segnities007.ui.card.UncheckedItemsCard
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
