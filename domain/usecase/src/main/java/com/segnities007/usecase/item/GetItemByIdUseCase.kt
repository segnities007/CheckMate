package com.segnities007.usecase.item

import com.segnities007.model.item.Item
import com.segnities007.repository.ItemRepository

/**
 * アイテムをIDで取得するUse Case
 */
class GetItemByIdUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(id: Int): Item? {
        return itemRepository.getItemById(id)
    }
}
