package com.segnities007.setting.mvi

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.segnities007.repository.BackupRepository
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.ui.mvi.MviState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.io.File
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log

class SettingViewModel(
    private val backupRepository: BackupRepository,
    private val appContext: Context,
) : BaseViewModel<SettingIntent, MviState, SettingEffect>(object : MviState {}) {

    override suspend fun handleIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.ShowToast -> showToast(intent)
            SettingIntent.ExportData -> exportData()
            is SettingIntent.ImportData -> importData(intent)
        }
    }

    private fun showToast(intent: SettingIntent.ShowToast) {
        sendEffect { SettingEffect.ShowToast(intent.message) }
    }

    private fun exportData() {
        viewModelScope.launch {
            try {
                val jsonString = backupRepository.exportData()
                saveToDownloads("backup.json", jsonString)
                sendEffect { SettingEffect.ShowToast("バックアップ完了") }
            } catch (e: Exception) {
                sendEffect { SettingEffect.ShowToast("バックアップ失敗: ${e.message}") }
                Log.e("SettingViewModel", "バックアップ失敗", e)
            }
        }
    }

    private fun importData(intent: SettingIntent.ImportData) {
        viewModelScope.launch {
            try {
                val jsonString = withContext(Dispatchers.IO) {
                    appContext.contentResolver.openInputStream(intent.uri)?.bufferedReader()?.use { it.readText() }
                        ?: throw IllegalStateException("ファイルが読み込めません")
                }
                backupRepository.importData(jsonString)
                sendEffect { SettingEffect.ShowToast("インポート完了") }
            } catch (e: Exception) {
                sendEffect { SettingEffect.ShowToast("インポート失敗: ${e.message}") }
            }
        }
    }


    private suspend fun saveToDownloads(fileName: String, jsonString: String) {
        withContext(Dispatchers.IO) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val resolver = appContext.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
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
