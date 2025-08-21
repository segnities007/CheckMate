package com.segnities007.model.item

import kotlinx.serialization.Serializable

@Serializable
data class ItemCheckState(
    val id: Int = 0,
    val itemId: Int = 0,
    val history: List<ItemCheckRecord> = emptyList(),
)
