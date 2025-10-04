package com.segnities007.usecase.checkstate

import com.segnities007.repository.ItemCheckStateRepository

/**
 * 全チェック状態を削除するUse Case
 */
class ClearAllCheckStatesUseCase(
    private val itemCheckStateRepository: ItemCheckStateRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            itemCheckStateRepository.clearAllCheckStates()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
