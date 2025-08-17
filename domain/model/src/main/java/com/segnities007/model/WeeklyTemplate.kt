package com.segnities007.model

data class WeeklyTemplate(
    val id: Int = 0,
    val dayOfWeek: WeekDay,
    val itemIds: List<Int> = emptyList()
)
