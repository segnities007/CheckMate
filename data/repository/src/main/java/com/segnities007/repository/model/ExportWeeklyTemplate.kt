package com.segnities007.repository.model

import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import kotlinx.serialization.Serializable

@Serializable
data class ExportWeeklyTemplate(
    val id: Int = 0,
    val title: String = "",
    val daysOfWeek: Set<String> = emptySet(),
    val itemIds: List<Int> = emptyList()
)

fun WeeklyTemplate.toExport(): ExportWeeklyTemplate = ExportWeeklyTemplate(
    id = id,
    title = title,
    daysOfWeek = daysOfWeek.map { it.name }.toSet(),
    itemIds = itemIds
)

fun ExportWeeklyTemplate.toDomain(): WeeklyTemplate = WeeklyTemplate(
    id = id,
    title = title,
    daysOfWeek = daysOfWeek.mapNotNull { name ->
        try { DayOfWeek.valueOf(name) } catch (_: Exception) { null }
    }.toSet(),
    itemIds = itemIds
)
