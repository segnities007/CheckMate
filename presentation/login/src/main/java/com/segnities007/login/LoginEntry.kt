package com.segnities007.login

import androidx.navigation3.runtime.EntryProviderScope
import com.segnities007.navigation.NavKey

fun EntryProviderScope<NavKey>.loginEntry(topNavigate: (NavKey) -> Unit) {
    entry<NavKey.Login> {
        LoginScreen(topNavigate = topNavigate)
    }
}
