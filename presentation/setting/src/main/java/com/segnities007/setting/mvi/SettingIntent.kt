package com.segnities007.setting.mvi

import android.net.Uri
import com.segnities007.navigation.Route
import com.segnities007.ui.mvi.MviIntent

sealed interface SettingIntent : MviIntent {
    data class ShowToast(
        val message: String,
    ) : SettingIntent

    data object ExportData : SettingIntent

    data class ImportData(
        val uri: Uri,
    ) : SettingIntent

    data object DeleteAllData : SettingIntent

    data object ConfirmDeleteAllData : SettingIntent

    data object CancelDeleteAllData : SettingIntent

    data object LinkWithGoogle : SettingIntent

    data object ChangeGoogleAccount : SettingIntent

    // ICSファイル関連
    data class ImportIcsFile(
        val uri: Uri,
    ) : SettingIntent

    data object ShowIcsImportDialog : SettingIntent

    data object HideIcsImportDialog : SettingIntent
}
