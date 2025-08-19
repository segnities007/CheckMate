package com.segnities007.model.item

data class ItemCheckState(
    val id: Int = 0,
    val itemId: Int = 0,
    val history: List<ItemCheckRecord> = emptyList(),
)
