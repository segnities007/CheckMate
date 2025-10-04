package com.segnities007.usecase.backup

import com.segnities007.repository.BackupRepository

/**
 * データをインポートするUse Case
 */
class ImportDataUseCase(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(jsonString: String): Result<Unit> {
        return try {
            backupRepository.importData(jsonString)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
