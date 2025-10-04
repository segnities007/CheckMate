package com.segnities007.usecase.checkstate

import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.ItemCheckStateRepository

/**
 * アイテムのチェック状態を取得するUse Case
 */
class GetCheckStateForItemUseCase(
    private val itemCheckStateRepository: ItemCheckStateRepository
) {
    suspend operator fun invoke(itemId: Int): Result<ItemCheckState?> {
        return try {
            val state = itemCheckStateRepository.getCheckStateForItem(itemId)
            Result.success(state)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
