package com.segnities007.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.DayOfWeek as KDayOfWeek

@Composable
fun MonthlyCalendarCard(
    year: Int,
    month: Int,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            CalendarHeader(year = year, month = month)

            Spacer(modifier = Modifier.height(8.dp))

            CalendarWeekdayRow()

            Spacer(modifier = Modifier.height(8.dp))

            CalendarDateGrid(
                year = year,
                month = month,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    year: Int,
    month: Int,
) {
    Text(
        text = "${year}年 ${month}月",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(8.dp),
    )
}

@Composable
private fun CalendarWeekdayRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("日", "月", "火", "水", "木", "金", "土").forEach { day ->
            val textColor =
                when (day) {
                    "日" -> Color.Red
                    "土" -> Color.Blue
                    else -> MaterialTheme.colorScheme.onSurface
                }
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun CalendarDateGrid(
    year: Int,
    month: Int,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
) {
    val firstOfMonth = LocalDate(year, month, 1)
    val totalDays = firstOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).dayOfMonth
    val firstWeekday = (firstOfMonth.dayOfWeek.isoDayNumber % 7)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(300.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(firstWeekday) {
            Box(modifier = Modifier.size(40.dp)) // 空白セル
        }

        items(totalDays) { index ->
            val day = index + 1
            val date = LocalDate(year, month, day)
            CalendarDateCell(
                date = date,
                day = day,
                isSelected = date == selectedDate,
                onDateSelected = onDateSelected,
            )
        }
    }
}

@Composable
private fun CalendarDateCell(
    date: LocalDate,
    day: Int,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit,
) {
    val textColor =
        when (date.dayOfWeek) {
            KDayOfWeek.SUNDAY -> Color.Red
            KDayOfWeek.SATURDAY -> Color.Blue
            else -> MaterialTheme.colorScheme.onSurface
        }

    Box(
        modifier =
            Modifier
                .size(40.dp)
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ).clickable { onDateSelected(date) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "$day",
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else textColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun MonthlyCalendarCardPreview() {
    MonthlyCalendarCard(
        year = 2024,
        month = 4,
        selectedDate = LocalDate(2024, 4, 15),
        onDateSelected = {},
    )
}
