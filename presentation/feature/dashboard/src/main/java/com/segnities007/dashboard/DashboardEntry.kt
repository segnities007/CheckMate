package com.segnities007.dashboard

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.segnities007.navigation.NavKeys


fun EntryProviderScope<NavKey>.dashboardEntry(
    onNavigate: (NavKeys) -> Unit,
    onBack: () -> Unit,
) {
    entry<NavKeys.Hub.DashboardKey> {
        DashboardScreen(onNavigate = onNavigate)
    }
}