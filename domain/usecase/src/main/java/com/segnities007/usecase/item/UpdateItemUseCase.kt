package com.segnities007.usecase.item

import com.segnities007.model.item.Item
import com.segnities007.repository.ItemRepository

/**
 * アイテムを更新するUse Case
 * RoomのinsertItemが更新も兼ねている
 */
class UpdateItemUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(item: Item): Result<Unit> {
        return try {
            // バリデーション
            if (item.name.isBlank()) {
                return Result.failure(IllegalArgumentException("アイテム名は必須です"))
            }
            
            // 更新（insertItemが更新も兼ねている）
            itemRepository.insertItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
