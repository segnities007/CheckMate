package com.segnities007.model

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyTemplate(
    val id: Int = 0,
    val title: String = "",
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val itemIds: List<Int> = emptyList(),
)
