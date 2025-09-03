package com.segnities007.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.divider.HorizontalDividerWithLabel

@Composable
fun CategoryBasedItemList(
    allItems: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    onCheckItem: (itemId: Int, checked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val allItemIdsForSelectedDate = allItems.map { it.id }.distinct()
    val selectedItems = allItems.filter { allItemIdsForSelectedDate.contains(it.id) }

    // カテゴリ別にアイテムをグループ化
    val itemsByCategory = selectedItems.groupBy { it.category }

    Column(
        modifier = modifier,
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

