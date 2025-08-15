package com.segnities007.repository

import com.segnities007.local.dao.ItemDao
import com.segnities007.local.entity.toDomain
import com.segnities007.local.entity.toEntity
import com.segnities007.model.Item

class ItemRepositoryImpl(
    private val dao: ItemDao
) : ItemRepository {

    override suspend fun getAllItems(): List<Item> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun getItemById(id: String): Item? {
        return dao.getById(id)?.toDomain()
    }


    override suspend fun insertItem(item: Item) {
        dao.insert(item.toEntity())
    }

    override suspend fun deleteItem(id: String) {
        dao.deleteById(id)
    }
}