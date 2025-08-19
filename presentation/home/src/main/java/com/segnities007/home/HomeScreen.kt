package com.segnities007.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.home.component.MonthlyCalendarWithWeeklyTemplate
import com.segnities007.home.mvi.HomeIntent
import com.segnities007.home.mvi.HomeViewModel
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

    // トップバー・FAB・ナビバー設定
    LaunchedEffect(Unit) {
        setTopBar {}
        setFab {}
        setNavigationBar {
            FloatingNavigationBar(
                currentHubRoute = HubRoute.Home,
                onNavigate = onNavigate,
            )
        }
        Log.d("HomeScreen", state.toString())
    }

    Column(
        modifier =
            Modifier
                .verticalScroll(scrollState)
                .padding(top = innerPadding.calculateTopPadding()),
    ) {
        MonthlyCalendarWithWeeklyTemplate(
            selectedDate = state.selectedDate,
            templates = state.templatesForToday,
            allItems = state.itemsForToday,
            itemCheckStates = state.itemCheckStates,
            onCheckItem = { itemId, checked ->
                homeViewModel.sendIntent(HomeIntent.CheckItem(itemId, checked))
            },
            onDateSelected = { date ->
                homeViewModel.sendIntent(HomeIntent.SelectDate(date))
            },
        )
        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}
