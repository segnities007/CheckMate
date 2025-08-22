package com.segnities007.home

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.home.mvi.HomeIntent
import com.segnities007.home.mvi.HomeViewModel
import com.segnities007.home.page.EnhancedHomeContent
import com.segnities007.navigation.HubRoute
import com.segnities007.ui.bar.FloatingNavigationBar
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val homeViewModel: HomeViewModel = koinInject()
    val state by homeViewModel.state.collectAsState()
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
        label = "navigationBarAlpha"
    )

    LaunchedEffect(Unit) {
        setTopBar {}
        setFab {}
        setNavigationBar {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = HubRoute.Home,
                onNavigate = onNavigate,
            )
        }
        Log.d("HomeScreen", state.toString())
    }

    Column(
        modifier = Modifier.padding(horizontal = 0.dp),
    ) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        
        EnhancedHomeContent(
            innerPadding = innerPadding,
            selectedDate = state.selectedDate,
            currentYear = state.currentYear,
            currentMonth = state.currentMonth,
            templates = state.templatesForToday,
            allItems = state.itemsForToday,
            itemCheckStates = state.itemCheckStates,
            onCheckItem = { itemId, checked ->
                homeViewModel.sendIntent(HomeIntent.CheckItem(itemId, checked))
            },
            onDateSelected = { date ->
                homeViewModel.sendIntent(HomeIntent.SelectDate(date))
            },
            onMonthChanged = { year, month ->
                // 月が変更されたときの処理はEnhancedCalendarCard内で行われる
            },
            sendIntent = homeViewModel::sendIntent,
            scrollState = scrollState
        )
        
        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}
