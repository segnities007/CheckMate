package com.segnities007.home.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.segnities007.model.Item
import java.time.LocalDate
import java.time.YearMonth
import kotlin.time.ExperimentalTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalTime::class)
@Composable
fun MonthlyCalendarWithItems(
    yearMonth: YearMonth,
    itemsForDate: Map<LocalDate, List<Item>>,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // 月カレンダー
        MonthlyCalendar(
            yearMonth = yearMonth,
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
                onDateSelected(it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 選択日アイテム
        selectedDate?.let { date ->
            Text(
                "今日の忘れ物防止アイテム",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            val itemList = itemsForDate[date].orEmpty()

            if (itemList.isEmpty()) {
                Text(
                    "アイテムはありません",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemList) { item ->
                            ItemRowWithCheckbox(item = item)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun ItemRowWithCheckbox(item: Item) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it }
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (!item.imagePath.isNullOrEmpty()) {
            AsyncImage(
                model = item.imagePath,
                contentDescription = item.name,
                modifier = Modifier
                    .size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyLarge)
            item.description?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyCalendar(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${yearMonth.year}年 ${yearMonth.monthValue}月",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("月","火","水","木","金","土","日").forEach { day ->
                Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val firstDayOfMonth = (yearMonth.atDay(1).dayOfWeek.value % 7)
        val totalDays = yearMonth.lengthOfMonth()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(firstDayOfMonth) { Box(modifier = Modifier.size(40.dp)) }

            items(totalDays) { index ->
                val day = index + 1
                val date = yearMonth.atDay(day)
                val isSelected = date == selectedDate
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$day",
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun MonthlyCalendarWithItemsPreview() {
    val today = LocalDate.now()
    val dummyItems = listOf(
        Item(id = 1, name = "財布", description = "忘れずに持っていく"),
        Item(id = 2, name = "鍵", description = "自宅と学校"),
        Item(id = 3, name = "水筒", description = "飲み物")
    )
    val itemsMap = mapOf(today to dummyItems)

    MaterialTheme {
        MonthlyCalendarWithItems(
            yearMonth = YearMonth.now(),
            itemsForDate = itemsMap,
            onDateSelected = {}
        )
    }
}
