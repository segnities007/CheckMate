package com.segnities007.repository

import com.segnities007.model.Item

interface ItemRepository {
    suspend fun getAllItems(): List<Item>
    suspend fun getItemById(id: String): Item?
    suspend fun insertItem(item: Item)
    suspend fun deleteItem(id: String)
}
