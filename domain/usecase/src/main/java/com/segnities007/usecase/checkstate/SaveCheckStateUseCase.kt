package com.segnities007.usecase.checkstate

import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.ItemCheckStateRepository

/**
 * チェック状態を保存するUse Case
 */
class SaveCheckStateUseCase(
    private val itemCheckStateRepository: ItemCheckStateRepository
) {
    suspend operator fun invoke(checkState: ItemCheckState): Result<Unit> {
        return try {
            itemCheckStateRepository.saveCheckState(checkState)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
