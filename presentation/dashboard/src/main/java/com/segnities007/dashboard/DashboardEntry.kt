package com.segnities007.dashboard

import androidx.navigation3.runtime.EntryProviderScope
import com.segnities007.navigation.NavKey

fun EntryProviderScope<NavKey>.dashboardEntry(onNavigate: (NavKey) -> Unit) {
    entry<NavKey.Dashboard> {
        DashboardScreen(onNavigate = onNavigate)
    }
}