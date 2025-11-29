package com.segnities007.templates

import androidx.navigation3.runtime.EntryProviderScope
import com.segnities007.navigation.NavKey

fun EntryProviderScope<NavKey>.templatesEntry(onNavigate: (NavKey) -> Unit) {
    entry<NavKey.Templates> {
        TemplatesScreen(onNavigate = onNavigate)
    }
}
