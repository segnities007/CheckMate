package com.segnities007.usecase.checkstate

import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.ItemCheckStateRepository

/**
 * アイテムのチェック状態を取得するUse Case
 */
class GetCheckStateForItemUseCase(
    private val itemCheckStateRepository: ItemCheckStateRepository
) {
    suspend operator fun invoke(itemId: Int): ItemCheckState? {
        return itemCheckStateRepository.getCheckStateForItem(itemId)
    }
}
