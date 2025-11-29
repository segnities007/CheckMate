package com.segnities007.splash

import androidx.navigation3.runtime.EntryProviderScope
import com.segnities007.navigation.NavKey

fun EntryProviderScope<NavKey>.splashEntry() {
    entry<NavKey.Splash> {
        SplashScreen()
    }
}
