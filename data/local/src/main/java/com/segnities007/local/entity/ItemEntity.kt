package com.segnities007.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val category: String,
    val imagePath: String,
    val createdAt: String,
)

@OptIn(ExperimentalTime::class)
fun Item.toEntity(): ItemEntity =
    ItemEntity(
        id = id,
        name = name,
        description = description,
        category = category.name,
        imagePath = imagePath,
        createdAt = createdAt.toString(),
    )

@OptIn(ExperimentalTime::class)
fun ItemEntity.toDomain(): Item =
    Item(
        id = id,
        name = name,
        description = description,
        category = ItemCategory.valueOf(category),
        imagePath = imagePath,
        createdAt = Instant.parse(createdAt),
    )
