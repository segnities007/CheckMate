package com.segnities007.items.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.SortOrder
import com.segnities007.model.item.ItemCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterBar(
    searchQuery: String,
    selectedCategory: ItemCategory?,
    sortOrder: SortOrder,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (ItemCategory?) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 検索バー
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 8.dp), // 12dp → 8dpに変更
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("アイテムを検索", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        // フィルタとソートボタン
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // カテゴリフィルタ
            Box(
                modifier = Modifier.weight(1f), // 1:1の比率を保つ
            ) {
                OutlinedButton(
                    onClick = { showCategoryMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            )
                        )
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = getCategoryDisplayName(selectedCategory),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .clip(RoundedCornerShape(12.dp)),
                ) {
                    DropdownMenuItem(
                        text = { Text("全カテゴリ", style = MaterialTheme.typography.bodySmall) },
                        onClick = {
                            onCategoryChange(null)
                            showCategoryMenu = false
                        },
                    )
                    ItemCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(getCategoryDisplayName(category), style = MaterialTheme.typography.bodySmall) },
                            onClick = {
                                onCategoryChange(category)
                                showCategoryMenu = false
                            },
                        )
                    }
                }
            }

            // 並び替え
            Box(
                modifier = Modifier.weight(1f), // 1:1の比率を保つ
            ) {
                OutlinedButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.fillMaxWidth(), // 幅を最大に
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            )
                        )
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sort,
                            contentDescription = "Sort",
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = getSortOrderShortName(sortOrder),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .clip(RoundedCornerShape(12.dp)),
                ) {
                    DropdownMenuItem(
                        text = { Text("名前順", style = MaterialTheme.typography.bodySmall) },
                        onClick = {
                            onSortOrderChange(SortOrder.NAME_ASC)
                            showSortMenu = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("日時順", style = MaterialTheme.typography.bodySmall) },
                        onClick = {
                            onSortOrderChange(SortOrder.CREATED_DESC)
                            showSortMenu = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("カテゴリ順", style = MaterialTheme.typography.bodySmall) },
                        onClick = {
                            onSortOrderChange(SortOrder.CATEGORY_ASC)
                            showSortMenu = false
                        },
                    )
                }
            }
        }
    }
}

private fun getSortOrderShortName(sortOrder: SortOrder): String {
    return when (sortOrder) {
        SortOrder.NAME_ASC -> "名前順"
        SortOrder.NAME_DESC -> "名前順"
        SortOrder.CREATED_ASC -> "日時順"
        SortOrder.CREATED_DESC -> "日時順"
        SortOrder.CATEGORY_ASC -> "カテゴリ順"
        SortOrder.CATEGORY_DESC -> "カテゴリ順"
    }
}

private fun getCategoryDisplayName(category: ItemCategory?): String {
    return when (category) {
        null -> "全カテゴリ"
        ItemCategory.STUDY_SUPPLIES -> "学業用品"
        ItemCategory.DAILY_SUPPLIES -> "生活用品"
        ItemCategory.CLOTHING_SUPPLIES -> "衣類用品"
        ItemCategory.FOOD_SUPPLIES -> "食事用品"
        ItemCategory.HEALTH_SUPPLIES -> "健康用品"
        ItemCategory.BEAUTY_SUPPLIES -> "美容用品"
        ItemCategory.EVENT_SUPPLIES -> "イベント用品"
        ItemCategory.HOBBY_SUPPLIES -> "趣味用品"
        ItemCategory.TRANSPORT_SUPPLIES -> "交通用品"
        ItemCategory.CHARGING_SUPPLIES -> "充電用品"
        ItemCategory.WEATHER_SUPPLIES -> "天候対策用品"
        ItemCategory.ID_SUPPLIES -> "証明用品"
        ItemCategory.OTHER_SUPPLIES -> "その他用品"
    }
}
