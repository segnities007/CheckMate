package com.segnities007.setting.mvi

import com.segnities007.ui.mvi.MviReducer

class SettingReducer : MviReducer<SettingState, SettingIntent> {
    override fun reduce(currentState: SettingState, intent: SettingIntent): SettingState {
        return when (intent) {
            is SettingIntent.ShowIcsImportDialog -> currentState.copy(showIcsImportDialog = true)
            is SettingIntent.HideIcsImportDialog -> currentState.copy(showIcsImportDialog = false)
            is SettingIntent.ShowToast -> currentState // Effectのみ、状態変化なし
            is SettingIntent.DeleteAllData -> currentState.copy(showDeleteAllDataDialog = true)
            is SettingIntent.CancelDeleteAllData -> currentState.copy(showDeleteAllDataDialog = false)
            is SettingIntent.ConfirmDeleteAllData -> currentState.copy(showDeleteAllDataDialog = false)
            is SettingIntent.LinkWithGoogle -> currentState // Effectのみ、状態変化なし
            is SettingIntent.ChangeGoogleAccount -> currentState // Effectのみ、状態変化なし
            is SettingIntent.ImportIcsFile -> currentState.copy(isImportingIcs = true)
            is SettingIntent.ImportData -> currentState // Effectのみ、状態変化なし
                is SettingIntent.ExportData -> currentState // Effectのみ、状態変化なし
                else -> currentState
        }
    }
}
