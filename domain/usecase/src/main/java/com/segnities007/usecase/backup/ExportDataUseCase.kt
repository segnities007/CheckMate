package com.segnities007.usecase.backup

import com.segnities007.repository.BackupRepository

/**
 * データをエクスポートするUse Case
 */
class ExportDataUseCase(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(): Result<String> {
        return try {
            val jsonString = backupRepository.exportData()
            Result.success(jsonString)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
