package com.segnities007.usecase.item

import com.segnities007.model.item.Item
import com.segnities007.repository.ItemRepository

/**
 * アイテムを追加するUse Case
 * ビジネスロジック: バリデーション + 保存
 */
class AddItemUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(item: Item): Result<Unit> {
        return try {
            // バリデーション
            if (item.name.isBlank()) {
                return Result.failure(IllegalArgumentException("アイテム名は必須です"))
            }
            
            // 保存
            itemRepository.insertItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
