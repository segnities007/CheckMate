package com.segnities007.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.model.WeeklyTemplate
import kotlinx.datetime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
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
) {
    val today = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val start = centerDate.minus(3, DateTimeUnit.DAY)
    val days: List<LocalDate> = (0..6).map { start.plus(it, DateTimeUnit.DAY) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            WeekRangeHeader(
                first = days.first(),
                onPreviousWeek = { onCenterDateChanged(centerDate.minus(7, DateTimeUnit.DAY)) },
                onNextWeek = { onCenterDateChanged(centerDate.plus(7, DateTimeUnit.DAY)) },
            )

            WeekdayLabelsRow(days = days)

            WeekDaysRow(
                days = days,
                selectedDate = selectedDate,
                today = today,
                templates = templates,
                onDateSelected = onDateSelected,
            )

            if (selectedDate != today) {
                OutlinedButton(onClick = { onDateSelected(today); onCenterDateChanged(today) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.Today, contentDescription = "今日", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "今日に戻る", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun WeekCalendarDayChip(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasTemplates: Boolean,
    onClick: () -> Unit,
) {
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
        date.dayOfWeek == DayOfWeek.SATURDAY -> Color.Blue
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = "${date.dayOfMonth}",
                color = textColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            )
            if (hasTemplates) {
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

private fun hasTemplateFor(date: LocalDate, templates: List<WeeklyTemplate>): Boolean {
    val modelDay = com.segnities007.model.DayOfWeek.valueOf(date.dayOfWeek.name)
    return templates.any { modelDay in it.daysOfWeek }
}

private fun japaneseWeekday(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
    DayOfWeek.SUNDAY -> "日"
    DayOfWeek.MONDAY -> "月"
    DayOfWeek.TUESDAY -> "火"
    DayOfWeek.WEDNESDAY -> "水"
    DayOfWeek.THURSDAY -> "木"
    DayOfWeek.FRIDAY -> "金"
    DayOfWeek.SATURDAY -> "土"
}

@Composable
private fun WeekRangeHeader(
    first: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onPreviousWeek,
            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "前の週",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = "${first.year}年${first.month.number}月",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            maxLines = 1,
        )

        IconButton(
            onClick = onNextWeek,
            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "次の週",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeekdayLabelsRow(days: List<LocalDate>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        days.forEach { date ->
            val label = japaneseWeekday(date.dayOfWeek)
            val color = when (date.dayOfWeek) {
                DayOfWeek.SUNDAY -> Color.Red
                DayOfWeek.SATURDAY -> Color.Blue
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    color = color,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun WeekDaysRow(
    days: List<LocalDate>,
    selectedDate: LocalDate?,
    today: LocalDate,
    templates: List<WeeklyTemplate>,
    onDateSelected: (LocalDate) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        days.forEach { date ->
            val isSelected = date == selectedDate
            val isToday = date == today
            val hasTemplates = hasTemplateFor(date, templates)

            Box(modifier = Modifier.weight(1f)) {
                WeekCalendarDayChip(
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
