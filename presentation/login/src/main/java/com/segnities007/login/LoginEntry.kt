package com.segnities007.login

import androidx.navigation3.runtime.EntryProviderScope
import com.segnities007.navigation.NavKey
import com.segnities007.navigation.Route

fun EntryProviderScope<NavKey>.loginEntry(topNavigate: (Route) -> Unit) {
    entry<NavKey.Login> {
        LoginScreen(topNavigate = topNavigate)
    }
}
