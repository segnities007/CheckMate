package com.segnities007.repository

import com.segnities007.model.item.BarcodeInfo
import com.segnities007.model.item.Item
import com.segnities007.model.item.ProductInfo

interface ItemRepository {
    suspend fun getAllItems(): List<Item>

    suspend fun getItemById(id: Int): Item?

    suspend fun insertItem(item: Item)

    suspend fun deleteItem(id: Int)

    suspend fun getUncheckedItemsForToday(): List<Item>
    
    suspend fun getProductInfoByBarcode(barcodeInfo: BarcodeInfo): ProductInfo?
}
