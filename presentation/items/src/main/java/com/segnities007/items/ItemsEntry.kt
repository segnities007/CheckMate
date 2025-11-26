package com.segnities007.items

import androidx.navigation3.runtime.EntryProviderScope
import com.segnities007.navigation.NavKey

fun EntryProviderScope<NavKey>.itemsEntry(onNavigate: (NavKey) -> Unit) {
    entry<NavKey.Items> {
        ItemsScreen(onNavigate = onNavigate)
    }
}
