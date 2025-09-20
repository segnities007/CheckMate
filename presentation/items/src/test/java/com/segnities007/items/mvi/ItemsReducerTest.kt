package com.segnities007.items.mvi

import org.junit.Assert.assertEquals
import org.junit.Test

class ItemsReducerTest {
    @Test
    fun updateSearchQuery_changesQuery() {
        val initial = ItemsUiState(searchQuery = "")
        val updated = ItemsReducer.reduce(initial, ItemsIntent.UpdateSearchQuery("abc"))
        assertEquals("abc", updated.searchQuery)
    }

    @Test
    fun setItems_replacesList() {
        val initial = ItemsUiState(items = emptyList())
        val newItems = listOf(com.segnities007.model.item.Item(id = 1, name = "Item1", description = "", imagePath = "", category = com.segnities007.model.item.ItemCategory.OTHER, createdAt = 0L))
        val updated = ItemsReducer.reduce(initial, ItemsIntent.SetItems(newItems))
        assertEquals(1, updated.items.size)
        assertEquals("Item1", updated.items.first().name)
    }
}
