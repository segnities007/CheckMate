package com.segnities007.setting

import androidx.navigation3.runtime.EntryProviderScope
import com.segnities007.navigation.NavKey

fun EntryProviderScope<NavKey>.settingEntry(onNavigate: (NavKey) -> Unit) {
    entry<NavKey.Setting> {
        SettingScreen(onNavigate = onNavigate)
    }
}
