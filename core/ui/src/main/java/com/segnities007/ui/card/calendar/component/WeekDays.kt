package com.segnities007.ui.card.calendar.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.model.WeeklyTemplate
import com.segnities007.ui.card.calendar.util.hasTemplateFor
import kotlinx.datetime.LocalDate

@Composable
fun CalendarWeekDays(
    days: List<LocalDate>,
    selectedDate: LocalDate?,
    today: LocalDate,
    templates: List<WeeklyTemplate>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        days.forEach { date ->
            val isSelected = date == selectedDate
            val isToday = date == today
            val hasTemplates = hasTemplateFor(date, templates)

            Box(modifier = Modifier.weight(1f)) {
                CalendarWeekDayChip(
                    date = date,
                    isSelected = isSelected,
                    isToday = isToday,
                    hasTemplates = hasTemplates,
                    onClick = { onDateSelected(date) },
                )
            }
        }
    }
}