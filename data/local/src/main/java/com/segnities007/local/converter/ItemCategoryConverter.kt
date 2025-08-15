package com.segnities007.local.converter

import androidx.room.TypeConverter
import com.segnities007.model.ItemCategory

class ItemCategoryConverter {
    @TypeConverter
    fun fromCategory(category: ItemCategory): String = category.name

    @TypeConverter
    fun toCategory(name: String): ItemCategory = ItemCategory.valueOf(name)
}