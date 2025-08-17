package com.segnities007.model

data class WeeklyTemplate(
    val id: Int = 0,
    val title: String = "",
    val dayOfWeek: WeekDay = WeekDay.UNSPECIFIED,
    val itemIds: List<Int> = emptyList(),
    val itemCheckStates: Map<Int, Boolean> = emptyMap(),
)
