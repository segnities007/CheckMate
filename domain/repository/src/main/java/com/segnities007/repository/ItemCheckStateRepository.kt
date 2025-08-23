package com.segnities007.repository

import com.segnities007.model.item.ItemCheckState
import kotlinx.datetime.LocalDate

interface ItemCheckStateRepository {
    suspend fun getCheckStateForItem(itemId: Int): ItemCheckState?

    suspend fun getCheckStatesForItems(itemIds: List<Int>): List<ItemCheckState>

    suspend fun saveCheckState(state: ItemCheckState)
    
    suspend fun clearAllCheckStates()
}
