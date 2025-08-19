package com.segnities007.model

data class WeeklyTemplate(
    val id: Int = 0,
    val title: String = "",
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val itemIds: List<Int> = emptyList(),
)
