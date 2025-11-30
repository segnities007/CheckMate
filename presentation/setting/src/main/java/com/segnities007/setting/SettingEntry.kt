package com.segnities007.setting

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.segnities007.navigation.NavKeys


fun EntryProviderScope<NavKey>.settingEntry(
    onNavigate: (NavKeys) -> Unit,
    onBack: () -> Unit,
) {
    entry<NavKeys.Hub.SettingKey> {
        SettingScreen(onNavigate = onNavigate)
    }
}
