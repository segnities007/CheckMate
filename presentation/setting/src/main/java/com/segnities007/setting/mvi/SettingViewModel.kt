package com.segnities007.setting.mvi

import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.ui.mvi.MviState
import org.koin.core.component.KoinComponent

class SettingViewModel: BaseViewModel<SettingIntent, MviState, SettingEffect>(object : MviState {}), KoinComponent {
    override suspend fun handleIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.ShowToast -> showToast(intent)
        }
    }

    private fun showToast(intent: SettingIntent.ShowToast) {
        sendEffect{ SettingEffect.ShowToast(intent.message) }
    }
}