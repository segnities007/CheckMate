package com.segnities007.repository

import com.segnities007.model.item.Item

interface ItemRepository {
    suspend fun getAllItems(): List<Item>

    suspend fun getItemById(id: Int): Item?

    suspend fun insertItem(item: Item)

    suspend fun deleteItem(id: Int)
}
