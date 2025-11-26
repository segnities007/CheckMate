package com.segnities007.ui.card.calendar.component

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

@Composable
fun WeekDays(
    days: List<LocalDate>,
    selectedDate: LocalDate?,
    today: LocalDate,
    templates: List<com.segnities007.model.WeeklyTemplate>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
) {
    // Implementation of WeekDays component
}