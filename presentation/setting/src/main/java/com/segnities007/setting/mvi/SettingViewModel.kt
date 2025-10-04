package com.segnities007.setting.mvi

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.backup.ExportDataUseCase
import com.segnities007.usecase.backup.ImportDataUseCase
import com.segnities007.usecase.checkstate.ClearAllCheckStatesUseCase
import com.segnities007.usecase.ics.GenerateTemplatesFromIcsUseCase
import com.segnities007.usecase.ics.SaveGeneratedTemplatesUseCase
import com.segnities007.usecase.item.ClearAllItemsUseCase
import com.segnities007.usecase.template.ClearAllTemplatesUseCase
import com.segnities007.usecase.user.GetUserStatusUseCase
import com.segnities007.usecase.user.LoginWithGoogleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.io.File

class SettingViewModel(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val clearAllItemsUseCase: ClearAllItemsUseCase,
    private val clearAllCheckStatesUseCase: ClearAllCheckStatesUseCase,
    private val clearAllTemplatesUseCase: ClearAllTemplatesUseCase,
    private val getUserStatusUseCase: GetUserStatusUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val generateTemplatesFromIcsUseCase: GenerateTemplatesFromIcsUseCase,
    private val saveGeneratedTemplatesUseCase: SaveGeneratedTemplatesUseCase,
    private val appContext: Context,
) : BaseViewModel<SettingIntent, SettingState, SettingEffect>(SettingState()) {

    init {
        sendIntent(SettingIntent.LoadUserStatus)
    }

    private suspend fun loadUserStatus() {
        getUserStatusUseCase().fold(
            onSuccess = { userStatus ->
                setState { copy(userStatus = userStatus) }
            },
            onFailure = { e ->
                // エラーはData層でログ出力される
            }
        )
    }

    override suspend fun handleIntent(intent: SettingIntent) {
        when (intent) {
            SettingIntent.LoadUserStatus -> loadUserStatus()
            is SettingIntent.ExportData -> exportData()
            is SettingIntent.ImportData -> importData(intent)
            is SettingIntent.DeleteAllData -> showDeleteAllDataConfirmation()
            is SettingIntent.ConfirmDeleteAllData -> confirmDeleteAllData()
            is SettingIntent.CancelDeleteAllData -> cancelDeleteAllData()
            is SettingIntent.LinkWithGoogle -> linkWithGoogle()
            is SettingIntent.ChangeGoogleAccount -> changeGoogleAccount()
            is SettingIntent.ShowToast -> showToast(intent)
            is SettingIntent.ImportIcsFile -> importIcsFile(intent)
            is SettingIntent.ShowIcsImportDialog -> showIcsImportDialog()
            is SettingIntent.HideIcsImportDialog -> hideIcsImportDialog()
        }
    }

    private fun showToast(intent: SettingIntent.ShowToast) {
        sendEffect { SettingEffect.ShowToast(intent.message) }
    }

    private suspend fun exportData() {
        val result = exportDataUseCase()
        result.fold(
            onSuccess = { jsonString ->
                try {
                    saveToDownloads("backup.json", jsonString)
                    sendEffect { SettingEffect.ShowToast("バックアップ完了") }
                } catch (e: Exception) {
                    sendEffect { SettingEffect.ShowToast("バックアップ失敗: ${e.message}") }
                }
            },
            onFailure = { e ->
                sendEffect { SettingEffect.ShowToast("バックアップ失敗: ${e.message}") }
            }
        )
    }

    private suspend fun importData(intent: SettingIntent.ImportData) {
        try {
            val jsonString =
                withContext(Dispatchers.IO) {
                    appContext.contentResolver
                        .openInputStream(intent.uri)
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: throw IllegalStateException("ファイルが読み込めません")
                }
            val result = importDataUseCase(jsonString)
            result.fold(
                onSuccess = {
                    sendEffect { SettingEffect.ShowToast("インポート完了") }
                },
                onFailure = { e ->
                    sendEffect { SettingEffect.ShowToast("インポート失敗: ${e.message}") }
                }
            )
        } catch (e: Exception) {
            sendEffect { SettingEffect.ShowToast("インポート失敗: ${e.message}") }
        }
    }

    private fun showDeleteAllDataConfirmation() {
        setState { reduce(SettingIntent.DeleteAllData) }
    }

    private suspend fun confirmDeleteAllData() {
        clearAllCheckStatesUseCase().getOrElse { e ->
            sendEffect { SettingEffect.ShowToast("チェック状態削除失敗: ${e.message}") }
            return
        }

        clearAllTemplatesUseCase().getOrElse { e ->
            sendEffect { SettingEffect.ShowToast("テンプレート削除失敗: ${e.message}") }
            return
        }

        clearAllItemsUseCase().getOrElse { e ->
            sendEffect { SettingEffect.ShowToast("アイテム削除失敗: ${e.message}") }
            return
        }

        setState { reduce(SettingIntent.ConfirmDeleteAllData) }
        sendEffect { SettingEffect.ShowToast("全データを削除しました") }
    }

    private fun cancelDeleteAllData() {
        setState { reduce(SettingIntent.CancelDeleteAllData) }
    }

    private suspend fun linkWithGoogle() {
        performGoogleAuthentication(
            successMessage = "Googleアカウントと連携しました",
            failureMessage = "Google連携に失敗しました"
        )
    }

    private suspend fun changeGoogleAccount() {
        performGoogleAuthentication(
            successMessage = "Googleアカウントを変更しました",
            failureMessage = "アカウント変更に失敗しました"
        )
    }

    private suspend fun performGoogleAuthentication(
        successMessage: String,
        failureMessage: String
    ) {
        loginWithGoogleUseCase().getOrElse { e ->
            sendEffect { SettingEffect.ShowToast(failureMessage) }
            return
        }

        val updatedUserStatus = getUserStatusUseCase().getOrElse { e ->
            sendEffect { SettingEffect.ShowToast(failureMessage) }
            return
        }

        setState { copy(userStatus = updatedUserStatus) }
        sendEffect { SettingEffect.ShowToast(successMessage) }
    }

    private suspend fun importIcsFile(intent: SettingIntent.ImportIcsFile) {
        setState { reduce(intent) }

        val templates = generateTemplatesFromIcsUseCase(intent.uri).getOrElse { e ->
            setState { copy(isImportingIcs = false) }
            sendEffect { SettingEffect.ShowToast("ICSファイルのインポートに失敗しました: ${e.message}") }
            return
        }

        saveGeneratedTemplatesUseCase(templates).getOrElse { e ->
            setState { copy(isImportingIcs = false) }
            sendEffect { SettingEffect.ShowToast("テンプレート保存に失敗しました: ${e.message}") }
            return
        }

        setState { copy(isImportingIcs = false, showIcsImportDialog = false) }
        sendEffect {
            SettingEffect.ShowIcsImportResult(
                successCount = templates.size,
                totalCount = templates.size,
            )
        }
    }

    private fun showIcsImportDialog() {
        setState { reduce(SettingIntent.ShowIcsImportDialog) }
    }

    private fun hideIcsImportDialog() {
        setState { reduce(SettingIntent.HideIcsImportDialog) }
    }

    private suspend fun saveToDownloads(
        fileName: String,
        jsonString: String,
    ) {
        withContext(Dispatchers.IO) {
            val contentValues =
                ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

            val resolver = appContext.contentResolver
            val uri =
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IllegalStateException("MediaStoreにファイル作成失敗")

            resolver.openOutputStream(uri)?.use { stream ->
                stream.write(jsonString.toByteArray())
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
    }
}

// =============================================================================
// Reducer Function
// =============================================================================

private fun SettingState.reduce(intent: SettingIntent): SettingState {
    return when (intent) {
        // ダイアログ表示関連
        SettingIntent.ShowIcsImportDialog -> copy(showIcsImportDialog = true)
        SettingIntent.HideIcsImportDialog -> copy(showIcsImportDialog = false)
        
        // データ削除確認
        SettingIntent.DeleteAllData -> copy(showDeleteAllDataDialog = true)
        SettingIntent.CancelDeleteAllData -> copy(showDeleteAllDataDialog = false)
        SettingIntent.ConfirmDeleteAllData -> copy(showDeleteAllDataDialog = false)
        
        // ICSインポート
        is SettingIntent.ImportIcsFile -> copy(isImportingIcs = true)
        
        // 他のIntentはViewModelで処理（非同期処理など）
        else -> this
    }
}
