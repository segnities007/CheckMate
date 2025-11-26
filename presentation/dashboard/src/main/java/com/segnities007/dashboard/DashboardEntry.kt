package com.segnities007.dashboard

import com.segnities007.navigation.NavKey
import androidx.navigation3.runtime.EntryProviderScope

fun EntryProviderScope<NavKey>.dashboardEntry() {
    entry<NavKey.Dashboard> {
    }
}