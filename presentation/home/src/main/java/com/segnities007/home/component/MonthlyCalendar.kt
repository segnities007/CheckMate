package com.segnities007.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DayOfWeek as KDayOfWeek

@Composable
fun MonthlyCalendarWithWeeklyTemplate(
    selectedDate: LocalDate,
    templates: List<WeeklyTemplate>,
    allItems: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MonthlyCalendar(
            year = selectedDate.year,
            month = selectedDate.monthNumber,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val allItemIdsForSelectedDate = templates.flatMap { it.itemIds }.distinct()
        val selectedItems = allItems.filter { allItemIdsForSelectedDate.contains(it.id) }

        if (selectedItems.isEmpty()) {
            Text(
                "アイテムはありません",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
            )
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                selectedItems.forEach { item ->
                    val isChecked = itemCheckStates[item.id] ?: false
                    ItemCard(
                        item = item,
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            onCheckItem(item.id, checked)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyCalendar(
    year: Int,
    month: Int,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
) {
    val firstOfMonth = LocalDate(year, month, 1)
    val totalDays = firstOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).dayOfMonth
    val firstWeekday = (firstOfMonth.dayOfWeek.isoDayNumber % 7) // 日曜=0

    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
        ) {
            Text(
                text = "${year}年 ${month}月",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            // 曜日ヘッダー
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
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

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // 空白セル
                items(firstWeekday) {
                    Box(modifier = Modifier.size(40.dp))
                }

                // 日付セル
                items(totalDays) { index ->
                    val day = index + 1
                    val date = LocalDate(year, month, day)
                    val isSelected = date == selectedDate

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
            }
        }
    }
}
