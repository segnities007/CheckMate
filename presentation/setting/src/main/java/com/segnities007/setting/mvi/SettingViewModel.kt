package com.segnities007.setting.mvi

import android.content.ContentValues
import android.content.Context
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



    override suspend fun handleIntent(intent: SettingIntent) {
        when (intent) {
            SettingIntent.LoadUserStatus -> loadUserStatus()
            is SettingIntent.ExportData -> exportData()
            is SettingIntent.ImportData -> importData(intent)
            is SettingIntent.DeleteAllData -> setState { copy(showDeleteAllDataDialog = true) }
            is SettingIntent.ConfirmDeleteAllData -> confirmDeleteAllData()
            is SettingIntent.CancelDeleteAllData -> setState { copy(showDeleteAllDataDialog = false) }
            is SettingIntent.LinkWithGoogle -> linkWithGoogle()
            is SettingIntent.ChangeGoogleAccount -> changeGoogleAccount()
            is SettingIntent.ShowToast -> showToast(intent)
            is SettingIntent.ImportIcsFile -> importIcsFile(intent)
            is SettingIntent.ShowIcsImportDialog -> setState { copy(showIcsImportDialog = true) }
            is SettingIntent.HideIcsImportDialog -> setState { copy(showIcsImportDialog = false) }
        }
    }

    private fun showToast(intent: SettingIntent.ShowToast) {
        sendEffect { SettingEffect.ShowToast(intent.message) }
    }

    private fun loadUserStatus() {
        execute(
            action = { getUserStatusUseCase().getOrThrow() },
            reducer = { userStatus -> copy(userStatus = userStatus) }
        )
    }

    private fun exportData() {
        viewModelScope.launch {
            exportDataUseCase().fold(
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
    }

    private fun importData(intent: SettingIntent.ImportData) {
        viewModelScope.launch {
            try {
                val jsonString = withContext(Dispatchers.IO) {
                    appContext.contentResolver.openInputStream(intent.uri)?.bufferedReader()?.use { it.readText() }
                        ?: throw IllegalStateException("ファイルが読み込めません")
                }
                importDataUseCase(jsonString).fold(
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
    }

    private fun confirmDeleteAllData() {
        viewModelScope.launch {
            val checkStateResult = clearAllCheckStatesUseCase()
            if (checkStateResult.isFailure) {
                sendEffect { SettingEffect.ShowToast("チェック状態削除失敗: ${checkStateResult.exceptionOrNull()?.message}") }
                return@launch
            }

            val templateResult = clearAllTemplatesUseCase()
            if (templateResult.isFailure) {
                sendEffect { SettingEffect.ShowToast("テンプレート削除失敗: ${templateResult.exceptionOrNull()?.message}") }
                return@launch
            }

            val itemResult = clearAllItemsUseCase()
            if (itemResult.isFailure) {
                sendEffect { SettingEffect.ShowToast("アイテム削除失敗: ${itemResult.exceptionOrNull()?.message}") }
                return@launch
            }

            setState { copy(showDeleteAllDataDialog = false) }
            sendEffect { SettingEffect.ShowToast("全データを削除しました") }
        }
    }

    private fun linkWithGoogle() {
        performGoogleAuthentication(
            successMessage = "Googleアカウントと連携しました",
            failureMessage = "Google連携に失敗しました"
        )
    }

    private fun changeGoogleAccount() {
        performGoogleAuthentication(
            successMessage = "Googleアカウントを変更しました",
            failureMessage = "アカウント変更に失敗しました"
        )
    }

    private fun performGoogleAuthentication(
        successMessage: String,
        failureMessage: String
    ) {
        viewModelScope.launch {
            loginWithGoogleUseCase().fold(
                onSuccess = {
                    getUserStatusUseCase().fold(
                        onSuccess = { updatedUserStatus ->
                            setState { copy(userStatus = updatedUserStatus) }
                            sendEffect { SettingEffect.ShowToast(successMessage) }
                        },
                        onFailure = {
                            sendEffect { SettingEffect.ShowToast(failureMessage) }
                        }
                    )
                },
                onFailure = {
                    sendEffect { SettingEffect.ShowToast(failureMessage) }
                }
            )
        }
    }

    private fun importIcsFile(intent: SettingIntent.ImportIcsFile) {
        setState { copy(isImportingIcs = true) }

        viewModelScope.launch {
            val templatesResult = generateTemplatesFromIcsUseCase(intent.uri)
            val templates = templatesResult.getOrElse { e ->
                setState { copy(isImportingIcs = false) }
                sendEffect { SettingEffect.ShowToast("ICSファイルのインポートに失敗しました: ${e.message}") }
                return@launch
            }

            saveGeneratedTemplatesUseCase(templates).fold(
                onSuccess = {
                    setState { copy(isImportingIcs = false, showIcsImportDialog = false) }
                    sendEffect {
                        SettingEffect.ShowIcsImportResult(
                            successCount = templates.size,
                            totalCount = templates.size,
                        )
                    }
                },
                onFailure = { e ->
                    setState { copy(isImportingIcs = false) }
                    sendEffect { SettingEffect.ShowToast("テンプレート保存に失敗しました: ${e.message}") }
                }
            )
        }
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
