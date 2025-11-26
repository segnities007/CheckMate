package com.segnities007.home

import androidx.navigation3.runtime.EntryProviderScope
import com.segnities007.navigation.NavKey

fun EntryProviderScope<NavKey>.homeEntry(onNavigate: (NavKey) -> Unit) {
    entry<NavKey.Home> {
        HomeScreen(onNavigate = onNavigate)
    }
}
