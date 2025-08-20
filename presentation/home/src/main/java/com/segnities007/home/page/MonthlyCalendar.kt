package com.segnities007.home.page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.home.component.ItemCard
import com.segnities007.home.component.MonthlyCalendarCard
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun MonthlyCalendarWithWeeklyTemplate(
    selectedDate: LocalDate,
    templates: List<WeeklyTemplate>,
    allItems: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val allItemIdsForSelectedDate = templates.flatMap { it.itemIds }.distinct()
        val selectedItems = allItems.filter { allItemIdsForSelectedDate.contains(it.id) }

        // カレンダー
        MonthlyCalendarCard(
            year = selectedDate.year,
            month = selectedDate.month.number,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
        )

        // 忘れ物アイテムリスト
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalDividerWithLabel("忘れ物アイテム")
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
