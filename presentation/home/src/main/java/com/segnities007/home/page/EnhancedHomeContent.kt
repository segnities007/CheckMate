package com.segnities007.home.page

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.segnities007.home.component.*
import com.segnities007.home.mvi.HomeIntent
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import kotlinx.datetime.LocalDate

@Composable
fun EnhancedHomeContent(
    selectedDate: LocalDate,
    currentYear: Int,
    currentMonth: Int,
    templates: List<WeeklyTemplate>,
    allItems: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (Int, Int) -> Unit,
    sendIntent: (HomeIntent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // カレンダー
        EnhancedCalendarCard(
            year = currentYear,
            month = currentMonth,
            selectedDate = selectedDate,
            templates = templates,
            onDateSelected = onDateSelected,
            onMonthChanged = { year, month ->
                onMonthChanged(year, month)
                sendIntent(HomeIntent.ChangeMonth(year, month))
            },
        )

        // 今日の進捗セクション
        HorizontalDividerWithLabel("今日の進捗")
        StatisticsCard(
            itemsForToday = allItems,
            itemCheckStates = itemCheckStates,
        )

        // カテゴリ別アイテムリスト
        CategoryBasedItemList(
            allItems = allItems,
            itemCheckStates = itemCheckStates,
            onCheckItem = onCheckItem,
        )
    }
}

@Composable
private fun CategoryBasedItemList(
    allItems: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
) {
    val allItemIdsForSelectedDate = allItems.map { it.id }.distinct()
    val selectedItems = allItems.filter { allItemIdsForSelectedDate.contains(it.id) }

    // カテゴリ別にアイテムをグループ化
    val itemsByCategory = selectedItems.groupBy { it.category }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // セクションヘッダー
        HorizontalDividerWithLabel("今日のアイテム")

        if (selectedItems.isEmpty()) {
            // アイテムがない場合の表示
            EmptyStateCard()
        } else {
            // カテゴリ別にアイテムを表示
            itemsByCategory.forEach { (category, items) ->
                val checkedCount = items.count { itemCheckStates[it.id] == true }

                CategoryGroupHeader(
                    category = category,
                    itemCount = items.size,
                    checkedCount = checkedCount,
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items.forEach { item ->
                        val isChecked = itemCheckStates[item.id] ?: false
                        EnhancedItemCard(
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
}

@Composable
private fun EmptyStateCard(modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation =
            CardDefaults.elevatedCardElevation(
                defaultElevation = 1.dp,
                pressedElevation = 2.dp,
                focusedElevation = 1.dp,
                hoveredElevation = 1.dp,
            ),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = "Empty",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = "今日のアイテムはありません",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = "テンプレートを作成してアイテムを追加してください",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
