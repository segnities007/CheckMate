package com.segnities007.ui.card.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.segnities007.model.WeeklyTemplate
import com.segnities007.ui.card.BaseCard
import com.segnities007.ui.card.calendar.component.CalendarWeekHeader
import com.segnities007.ui.card.calendar.component.WeekdayLabels
import com.segnities007.ui.card.calendar.component.WeekDays
import com.segnities007.ui.card.calendar.util.weekDaysStartingOn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun WeekCalendarCard(
    selectedDate: LocalDate?,
    templates: List<WeeklyTemplate>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    centerDate: LocalDate = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date,
    onCenterDateChanged: (LocalDate) -> Unit = {},
    weekStart: DayOfWeek = DayOfWeek.SUNDAY,
) {
    val today = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    var internalCenter by remember { mutableStateOf(centerDate) }
    LaunchedEffect(centerDate) { internalCenter = centerDate }
    LaunchedEffect(selectedDate) {
        selectedDate?.let { if (it != internalCenter) internalCenter = it }
    }

    val days: List<LocalDate> = weekDaysStartingOn(internalCenter, weekStart)

    BaseCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalendarWeekHeader(
                first = days.first(),
                onPreviousWeek = {
                    val newCenter = internalCenter.minus(7, DateTimeUnit.DAY)
                    internalCenter = newCenter
                    onCenterDateChanged(newCenter)
                },
                onNextWeek = {
                    val newCenter = internalCenter.plus(7, DateTimeUnit.DAY)
                    internalCenter = newCenter
                    onCenterDateChanged(newCenter)
                },
            )

            WeekdayLabels(days = days)

            WeekDays(
                days = days,
                selectedDate = selectedDate,
                today = today,
                templates = templates,
                onDateSelected = { date ->
                    onDateSelected(date)
                    internalCenter = date
                    onCenterDateChanged(date)
                },
            )

            if (selectedDate != today) {
                OutlinedButton(
                    onClick = {
                        val newCenter = today
                        onDateSelected(today)
                        internalCenter = newCenter
                        onCenterDateChanged(newCenter)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = "今日",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "今日に戻る",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun WeekCalendarCardPreview() {
    val today = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    WeekCalendarCard(
        selectedDate = today,
        templates = emptyList(),
        onDateSelected = {},
        onCenterDateChanged = {},
    )
}