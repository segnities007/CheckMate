package com.segnities007.usecase.item

import com.segnities007.model.item.Item
import com.segnities007.repository.ItemRepository

/**
 * 全てのアイテムを取得するUse Case
 * 単一責任: アイテム一覧の取得のみ
 */
class GetAllItemsUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(): List<Item> {
        return itemRepository.getAllItems()
    }
}
