package com.segnities007.ui.card.calendar.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate

@Composable
@Deprecated("Use WeekHeader in WeekHeader.kt", ReplaceWith("WeekHeader(first, onPreviousWeek, onNextWeek, modifier)"))
fun WeekRangeHeader(
    first: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CalendarWeekHeader(
        first = first,
        onPreviousWeek = onPreviousWeek,
        onNextWeek = onNextWeek,
        modifier = modifier,
    )
}