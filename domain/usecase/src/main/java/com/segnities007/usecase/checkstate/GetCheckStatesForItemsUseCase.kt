package com.segnities007.usecase.checkstate

import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.ItemCheckStateRepository

/**
 * 複数アイテムのチェック状態を取得するUse Case
 */
class GetCheckStatesForItemsUseCase(
    private val itemCheckStateRepository: ItemCheckStateRepository
) {
    suspend operator fun invoke(itemIds: List<Int>): List<ItemCheckState> {
        return itemCheckStateRepository.getCheckStatesForItems(itemIds)
    }
}
