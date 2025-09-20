package com.segnities007.setting.mvi

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.segnities007.repository.BackupRepository
import com.segnities007.repository.IcsTemplateRepository
import com.segnities007.repository.ItemCheckStateRepository
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.UserRepository
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.io.File

class SettingViewModel(
    private val backupRepository: BackupRepository,
    private val appContext: Context,
    private val itemRepository: ItemRepository,
    private val itemCheckStateRepository: ItemCheckStateRepository,
    private val weeklyTemplateRepository: WeeklyTemplateRepository,
    private val userRepository: UserRepository,
    private val icsTemplateRepository: IcsTemplateRepository,
) : BaseViewModel<SettingIntent, SettingState, SettingEffect>(SettingState()) {
    private val reducer: SettingReducer = SettingReducer()

    init {
        sendIntent(SettingIntent.LoadUserStatus)
    }

    private suspend fun loadUserStatus() {
        try {
            val userStatus = withContext(Dispatchers.IO) { userRepository.getUserStatus() }
            setState { copy(userStatus = userStatus) }
        } catch (e: Exception) {
            Log.e("SettingViewModel", "Failed to load user status", e)
        }
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
        try {
            val jsonString = withContext(Dispatchers.IO) { backupRepository.exportData() }
            saveToDownloads("backup.json", jsonString)
            sendEffect { SettingEffect.ShowToast("バックアップ完了") }
        } catch (e: Exception) {
            sendEffect { SettingEffect.ShowToast("バックアップ失敗: ${e.message}") }
            Log.e("SettingViewModel", "バックアップ失敗", e)
        }
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
            backupRepository.importData(jsonString)
            sendEffect { SettingEffect.ShowToast("インポート完了") }
        } catch (e: Exception) {
            sendEffect { SettingEffect.ShowToast("インポート失敗: ${e.message}") }
        }
    }

    private fun showDeleteAllDataConfirmation() {
        setState { reducer.reduce(this, SettingIntent.DeleteAllData) }
    }

    private suspend fun confirmDeleteAllData() {
        try {
            withContext(Dispatchers.IO) {
                // Repositoryを使用して全データを削除
                itemCheckStateRepository.clearAllCheckStates()
                weeklyTemplateRepository.clearAllTemplates()
                itemRepository.clearAllItems()
            }
            setState { reducer.reduce(this, SettingIntent.ConfirmDeleteAllData) }
            sendEffect { SettingEffect.ShowToast("全データを削除しました") }
        } catch (e: Exception) {
            sendEffect { SettingEffect.ShowToast("データ削除失敗: ${e.message}") }
            Log.e("SettingViewModel", "データ削除失敗", e)
        }
    }

    private fun cancelDeleteAllData() {
        setState { reducer.reduce(this, SettingIntent.CancelDeleteAllData) }
    }

    private suspend fun linkWithGoogle() {
        try {
            withContext(Dispatchers.IO) { userRepository.loginWithGoogle() }
            val updatedUserStatus = withContext(Dispatchers.IO) { userRepository.getUserStatus() }
            setState { copy(userStatus = updatedUserStatus) }
            sendEffect { SettingEffect.ShowToast("Googleアカウントと連携しました") }
        } catch (e: Exception) {
            sendEffect { SettingEffect.ShowToast("Google連携に失敗しました") }
        }
    }

    private suspend fun changeGoogleAccount() {
        try {
            withContext(Dispatchers.IO) { userRepository.loginWithGoogle() }
            val updatedUserStatus = withContext(Dispatchers.IO) { userRepository.getUserStatus() }
            setState { copy(userStatus = updatedUserStatus) }
            sendEffect { SettingEffect.ShowToast("Googleアカウントを変更しました") }
        } catch (e: Exception) {
            sendEffect { SettingEffect.ShowToast("アカウント変更に失敗しました") }
        }
    }

    private suspend fun importIcsFile(intent: SettingIntent.ImportIcsFile) {
        try {
            setState { reducer.reduce(this, intent) }

            val templates = withContext(Dispatchers.IO) { icsTemplateRepository.generateTemplatesFromIcs(intent.uri) }
            withContext(Dispatchers.IO) { icsTemplateRepository.saveGeneratedTemplates(templates) }

            setState { copy(isImportingIcs = false, showIcsImportDialog = false) }
            sendEffect {
                SettingEffect.ShowIcsImportResult(
                    successCount = templates.size,
                    totalCount = templates.size,
                )
            }
        } catch (e: Exception) {
            setState { copy(isImportingIcs = false) }
            sendEffect { SettingEffect.ShowToast("ICSファイルのインポートに失敗しました: ${e.message}") }
            Log.e("SettingViewModel", "ICSインポート失敗", e)
        }
    }

    private fun showIcsImportDialog() {
        setState { reducer.reduce(this, SettingIntent.ShowIcsImportDialog) }
    }

    private fun hideIcsImportDialog() {
        setState { reducer.reduce(this, SettingIntent.HideIcsImportDialog) }
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
