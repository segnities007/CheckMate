package com.segnities007.repository

interface BackupRepository {
    suspend fun exportData(): String

    suspend fun importData(jsonString: String)
}
