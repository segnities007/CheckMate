package com.segnities007.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.segnities007.local.converter.WeeklyTemplateConverters
import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate

@Entity(tableName = "weekly_templates")
@TypeConverters(WeeklyTemplateConverters::class)
data class WeeklyTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val itemStates: Map<Int, Boolean> = emptyMap()
)

fun WeeklyTemplateEntity.toDomain(): WeeklyTemplate =
    WeeklyTemplate(
        id = id,
        title = title,
        daysOfWeek = daysOfWeek,
        itemStates = itemStates,
    )

fun WeeklyTemplate.toEntity(): WeeklyTemplateEntity =
    WeeklyTemplateEntity(
        id = id,
        title = title,
        daysOfWeek = daysOfWeek,
        itemStates = itemStates,
    )
