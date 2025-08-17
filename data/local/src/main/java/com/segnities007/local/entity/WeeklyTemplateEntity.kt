package com.segnities007.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.segnities007.local.converter.IntListConverter
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.WeekDay

@Entity(tableName = "weekly_templates")
@TypeConverters(IntListConverter::class)
data class WeeklyTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: String,       // enum型に変更
    val itemIds: List<Int>        // ItemEntityのIDリスト
)

fun WeeklyTemplateEntity.toDomain(): WeeklyTemplate =
    WeeklyTemplate(
        id = id,
        dayOfWeek = WeekDay.valueOf(dayOfWeek),
        itemIds = itemIds
    )

fun WeeklyTemplate.toEntity(): WeeklyTemplateEntity =
    WeeklyTemplateEntity(
        id = id,
        dayOfWeek = dayOfWeek.name,
        itemIds = itemIds
    )
