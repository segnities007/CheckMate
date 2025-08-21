package com.segnities007.repository.model

import com.segnities007.local.entity.toDomain
import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.model.item.ItemCheckState
import kotlinx.serialization.Serializable

@Serializable
data class ExportItemCheckState(
    val id: Int = 0,
    val itemId: Int = 0,
    val history: List<ExportItemCheckRecord> = emptyList()
)

fun ItemCheckState.toExport(): ExportItemCheckState = ExportItemCheckState(
    id = id,
    itemId = itemId,
    history = history.map { it.toExport() }
)

fun ExportItemCheckState.toDomain(): ItemCheckState = ItemCheckState(
    id = id,
    itemId = itemId,
    history = history.map { it.toDomain() }
)