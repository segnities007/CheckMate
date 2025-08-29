package com.segnities007.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class WeeklyTemplate(
    val id: Int = 0,
    val title: String = "",
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val itemIds: List<Int> = emptyList(),
)
