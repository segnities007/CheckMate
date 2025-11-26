package com.segnities007.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.home.mvi.HomeIntent
import com.segnities007.home.mvi.HomeState
import com.segnities007.home.mvi.HomeViewModel
import com.segnities007.home.page.EnhancedHomeContent
import com.segnities007.navigation.NavKey
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.util.rememberScrollVisibility
import org.koin.compose.koinInject

import com.segnities007.ui.scaffold.CheckMateScaffold
import com.segnities007.ui.theme.checkMateBackgroundBrush

@Composable
fun HomeScreen(
    onNavigate: (NavKey) -> Unit,
) {
    val homeViewModel: HomeViewModel = koinInject()
    val state by homeViewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val isVisible by rememberScrollVisibility(scrollState = scrollState)
    val backgroundBrush = MaterialTheme.checkMateBackgroundBrush

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "navigationBarAlpha",
    )

    // Effect処理
    LaunchedEffect(Unit) {
        homeViewModel.effect.collect { effect ->
            when (effect) {
                is com.segnities007.home.mvi.HomeEffect.ShowError -> {
                    // TODO: Snackbarやダイアログで表示
                }
            }
        }
    }

    CheckMateScaffold(
        bottomBar = {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = NavKey.Home,
                onNavigate = onNavigate,
            )
        }
    ) { innerPadding ->
        HomeUi(
            innerPadding = innerPadding,
            state = state,
            scrollState = scrollState,
            brash = backgroundBrush,
            sendIntent = homeViewModel::sendIntent,
        )
    }
}

@Composable
private fun HomeUi(
    innerPadding: PaddingValues,
    state: HomeState,
    scrollState: ScrollState,
    brash: Brush,
    sendIntent: (HomeIntent) -> Unit,
){
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(brash)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))

        EnhancedHomeContent(
            selectedDate = state.selectedDate,
            currentWeekCenter = state.currentWeekCenter,
            templates = state.templatesForToday,
            allItems = state.itemsForToday,
            itemCheckStates = state.itemCheckStates,
            onCheckItem = { itemId, checked ->
                sendIntent(HomeIntent.CheckItem(itemId, checked))
            },
            onDateSelected = { date ->
                sendIntent(HomeIntent.SelectDate(date))
            },
            sendIntent = sendIntent,
        )
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
        HomeUi(
            innerPadding = PaddingValues(0.dp),
            state = HomeState(),
            scrollState = rememberScrollState(),
            brash = verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary.copy(0.6f),
                    )
                ),
            sendIntent = {},
        )
}
