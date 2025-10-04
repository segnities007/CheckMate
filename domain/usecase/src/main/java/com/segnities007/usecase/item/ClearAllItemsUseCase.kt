package com.segnities007.usecase.item

import com.segnities007.repository.ItemRepository

/**
 * 全アイテムを削除するUse Case
 */
class ClearAllItemsUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            itemRepository.clearAllItems()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
