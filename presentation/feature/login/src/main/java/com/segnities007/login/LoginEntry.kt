package com.segnities007.login

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.segnities007.navigation.NavKeys


fun EntryProviderScope<NavKey>.loginEntry(onNavigate: (NavKeys) -> Unit) {
    entry<NavKeys.Auth.LoginKey> {
        LoginScreen(topNavigate = onNavigate)
    }
}
