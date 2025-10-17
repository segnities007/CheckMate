package com.segnities007.usecase.item

import com.segnities007.repository.ItemRepository

/**
 * アイテムを削除するUse Case
 */
class DeleteItemUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return try {
            itemRepository.deleteItem(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
