package com.segnities007.setting.mvi

import com.segnities007.ui.mvi.MviReducer

class SettingReducer : MviReducer<SettingState, SettingIntent> {
    override fun reduce(currentState: SettingState, intent: SettingIntent): SettingState {
        return when (intent) {
            is SettingIntent.ShowIcsImportDialog -> currentState.copy(showIcsImportDialog = true)
            is SettingIntent.HideIcsImportDialog -> currentState.copy(showIcsImportDialog = false)
            is SettingIntent.DeleteAllData -> currentState.copy(showDeleteAllDataDialog = true)
            is SettingIntent.CancelDeleteAllData -> currentState.copy(showDeleteAllDataDialog = false)
            is SettingIntent.ConfirmDeleteAllData -> currentState.copy(showDeleteAllDataDialog = false)
            is SettingIntent.ImportIcsFile -> currentState.copy(isImportingIcs = true)
            // Effect-only intents: these do not modify state, but trigger side effects handled elsewhere.
            // They simply return the current state unchanged.
            is SettingIntent.ShowToast,
            is SettingIntent.LinkWithGoogle,
            is SettingIntent.ChangeGoogleAccount,
            is SettingIntent.ImportData,
            is SettingIntent.ExportData -> currentState
            else -> currentState
        }
    }
}
