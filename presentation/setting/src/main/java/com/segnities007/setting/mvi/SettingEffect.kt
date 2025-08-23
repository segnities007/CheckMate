package com.segnities007.setting.mvi

import com.segnities007.navigation.Route
import com.segnities007.ui.mvi.MviEffect

sealed interface SettingEffect : MviEffect {
    data class ShowToast(
        val message: String,
    ) : SettingEffect
    
    data class ShowIcsImportResult(
        val successCount: Int,
        val totalCount: Int,
    ) : SettingEffect
}
