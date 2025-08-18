package com.segnities007.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.segnities007.local.converter.WeeklyTemplateConverters
import com.segnities007.model.WeekDay
import com.segnities007.model.WeeklyTemplate

@Entity(tableName = "weekly_templates")
@TypeConverters(WeeklyTemplateConverters::class)
data class WeeklyTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dayOfWeek: String,
    val itemIds: List<Int> = emptyList(),
    val itemCheckStates: Map<Int, Boolean> = emptyMap(),
)

fun WeeklyTemplateEntity.toDomain(): WeeklyTemplate =
    WeeklyTemplate(
        id = id,
        title = title,
        dayOfWeek = WeekDay.valueOf(dayOfWeek),
        itemIds = itemIds,
        itemCheckStates = itemCheckStates,
    )

fun WeeklyTemplate.toEntity(): WeeklyTemplateEntity =
    WeeklyTemplateEntity(
        id = id,
        title = title,
        dayOfWeek = dayOfWeek.name,
        itemIds = itemIds,
        itemCheckStates = itemCheckStates,
    )
