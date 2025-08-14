package com.segnities007.setting.mvi

import com.segnities007.navigation.Route
import com.segnities007.ui.mvi.MviIntent

sealed interface SettingIntent: MviIntent {
    data class ShowToast(val message: String): SettingIntent
}