package com.segnities007.model.item

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ItemCheckState(
    val id: Int = 0,
    val itemId: Int = 0,
    val history: List<ItemCheckRecord> = emptyList(),
)
