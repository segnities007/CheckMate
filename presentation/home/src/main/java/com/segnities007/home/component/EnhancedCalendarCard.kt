package com.segnities007.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.model.WeeklyTemplate
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DayOfWeek as KDayOfWeek

@OptIn(ExperimentalTime::class)
@Composable
fun EnhancedCalendarCard(
    year: Int,
    month: Int,
    selectedDate: LocalDate?,
    templates: List<WeeklyTemplate>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 2.dp,
            focusedElevation = 1.dp,
            hoveredElevation = 1.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ヘッダー（月間ナビゲーション付き）
            CalendarHeader(
                year = year,
                month = month,
                onPreviousMonth = {
                    val newMonth = if (month == 1) 12 else month - 1
                    val newYear = if (month == 1) year - 1 else year
                    onMonthChanged(newYear, newMonth)
                },
                onNextMonth = {
                    val newMonth = if (month == 12) 1 else month + 1
                    val newYear = if (month == 12) year + 1 else year
                    onMonthChanged(newYear, newMonth)
                }
            )

            // 今日ボタン
            if (selectedDate != today) {
                OutlinedButton(
                    onClick = { onDateSelected(today) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            )
                        )
                    )
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
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 曜日ヘッダー
            CalendarWeekdayRow()

            // カレンダーグリッド
            EnhancedCalendarDateGrid(
                year = year,
                month = month,
                selectedDate = selectedDate,
                today = today,
                templates = templates,
                onDateSelected = onDateSelected,
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    year: Int,
    month: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "前月",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${year}年 ${month}月",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "次月",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarWeekdayRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("日", "月", "火", "水", "木", "金", "土").forEach { day ->
            val textColor =
                when (day) {
                    "日" -> Color.Red
                    "土" -> Color.Blue
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EnhancedCalendarDateGrid(
    year: Int,
    month: Int,
    selectedDate: LocalDate?,
    today: LocalDate,
    templates: List<WeeklyTemplate>,
    onDateSelected: (LocalDate) -> Unit,
) {
    val firstOfMonth = LocalDate(year, month, 1)
    val totalDays = firstOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).dayOfMonth
    val firstWeekday = (firstOfMonth.dayOfWeek.isoDayNumber % 7)

    // テンプレートがある日付を取得
    val datesWithTemplates = templates.flatMap { template ->
        template.daysOfWeek.map { dayOfWeek ->
            // 今月の該当する曜日の日付を計算
            val firstDayOfWeek = firstOfMonth.dayOfWeek
            val targetDayOfWeek = when (dayOfWeek.name) {
                "MONDAY" -> KDayOfWeek.MONDAY
                "TUESDAY" -> KDayOfWeek.TUESDAY
                "WEDNESDAY" -> KDayOfWeek.WEDNESDAY
                "THURSDAY" -> KDayOfWeek.THURSDAY
                "FRIDAY" -> KDayOfWeek.FRIDAY
                "SATURDAY" -> KDayOfWeek.SATURDAY
                "SUNDAY" -> KDayOfWeek.SUNDAY
                else -> KDayOfWeek.MONDAY
            }
            
            val daysToAdd = (targetDayOfWeek.isoDayNumber - firstDayOfWeek.isoDayNumber + 7) % 7
            val firstOccurrence = firstOfMonth.plus(daysToAdd, DateTimeUnit.DAY)
            
            // 今月の該当する曜日のすべての日付を生成
            (0..4).mapNotNull { week ->
                val date = firstOccurrence.plus(week * 7, DateTimeUnit.DAY)
                if (date.month == firstOfMonth.month) date else null
            }
        }.flatten()
    }.distinct()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(firstWeekday) {
            Box(modifier = Modifier.size(40.dp)) // 空白セル
        }

        items(totalDays) { index ->
            val day = index + 1
            val date = LocalDate(year, month, day)
            val hasTemplates = datesWithTemplates.contains(date)
            
            EnhancedCalendarDateCell(
                date = date,
                day = day,
                isSelected = date == selectedDate,
                isToday = date == today,
                hasTemplates = hasTemplates,
                onDateSelected = onDateSelected,
            )
        }
    }
}

@Composable
private fun EnhancedCalendarDateCell(
    date: LocalDate,
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasTemplates: Boolean,
    onDateSelected: (LocalDate) -> Unit,
) {
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        date.dayOfWeek == KDayOfWeek.SUNDAY -> Color.Red
        date.dayOfWeek == KDayOfWeek.SATURDAY -> Color.Blue
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onDateSelected(date) },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "$day",
                color = textColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            // テンプレートインジケーター
            if (hasTemplates) {
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}
