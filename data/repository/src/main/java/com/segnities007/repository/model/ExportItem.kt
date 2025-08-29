package com.segnities007.repository.model

import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.text.category
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class ExportItem
    @OptIn(ExperimentalTime::class)
    constructor(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val category: String = "OTHER_SUPPLIES",
        val imagePath: String = "",
        @Serializable(with = InstantSerializer::class)
        val createdAt: Instant = Instant.fromEpochMilliseconds(0),
    )

@OptIn(ExperimentalTime::class)
fun Item.toExport(): ExportItem =
    ExportItem(
        id = id,
        name = name,
        description = description,
        category = category.name,
        imagePath = imagePath,
        createdAt = createdAt,
    )

@OptIn(ExperimentalTime::class)
fun ExportItem.toDomain(): Item =
    Item(
        id = id,
        name = name,
        description = description,
        category =
            try {
                ItemCategory.valueOf(category)
            } catch (_: Exception) {
                ItemCategory.OTHER_SUPPLIES
            },
        imagePath = imagePath,
        createdAt = createdAt,
    )
