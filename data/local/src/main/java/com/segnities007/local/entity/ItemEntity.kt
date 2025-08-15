package com.segnities007.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.segnities007.model.DomainInstant
import com.segnities007.model.Item
import com.segnities007.model.ItemCategory

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val category: ItemCategory,
    val imagePath: String?,
    val createdAt: DomainInstant     // TypeConverter で Long に変換,
)

fun Item.toEntity(): ItemEntity = ItemEntity(
    id = id,
    name = name,
    description = description,
    category = category,
    imagePath = imagePath,
    createdAt = createdAt
)

fun ItemEntity.toDomain(): Item = Item(
    id = id,
    name = name,
    description = description,
    category = category,
    imagePath = imagePath,
    createdAt = createdAt
)
