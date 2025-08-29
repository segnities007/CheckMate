package com.segnities007.repository

import com.segnities007.local.dao.ItemCheckStateDao
import com.segnities007.local.entity.toDomain
import com.segnities007.local.entity.toEntity
import com.segnities007.model.item.ItemCheckState

class ItemCheckStateRepositoryImpl(
    private val dao: ItemCheckStateDao,
) : ItemCheckStateRepository {
    override suspend fun getCheckStateForItem(itemId: Int): ItemCheckState? = dao.getByItemId(itemId)?.toDomain()

    override suspend fun getCheckStatesForItems(itemIds: List<Int>): List<ItemCheckState> =
        itemIds.mapNotNull { dao.getByItemId(it)?.toDomain() }

    override suspend fun saveCheckState(state: ItemCheckState) {
        dao.insert(state.toEntity()) // insert は REPLACE なので更新も可能
    }

    override suspend fun clearAllCheckStates() {
        dao.clearAll()
    }
}
