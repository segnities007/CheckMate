package com.segnities007.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.segnities007.common.keys.NavKeys


fun EntryProviderScope<NavKey>.homeEntry(
    onNavigate: (NavKeys) -> Unit,
    onBack: () -> Unit,
) {
    entry<NavKeys.Hub.HomeKey> {
        HomeScreen(onNavigate = onNavigate)
    }
}