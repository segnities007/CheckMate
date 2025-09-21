package com.segnities007.items.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
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
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 検索バー
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "アイテムを検索...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "検索",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
            )

            // フィルタ・ソート行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // カテゴリフィルタ
                var categoryExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        border =
                            ButtonDefaults.outlinedButtonBorder.copy(
                                brush =
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors =
                                            listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            ),
                                    ),
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "カテゴリ",
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedCategory?.let { getCategoryDisplayName(it) } ?: "カテゴリ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    ) {
                        DropdownMenuItem(
                            text = { Text("すべて") },
                            onClick = {
                                onCategoryChange(null)
                                categoryExpanded = false
                            },
                        )
                        ItemCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(getCategoryDisplayName(category)) },
                                onClick = {
                                    onCategoryChange(category)
                                    categoryExpanded = false
                                },
                            )
                        }
                    }
                }

                // ソート
                var sortExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedButton(
                        onClick = { sortExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        border =
                            ButtonDefaults.outlinedButtonBorder.copy(
                                brush =
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors =
                                            listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            ),
                                    ),
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "ソート",
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text =
                                when (sortOrder) {
                                    SortOrder.NAME_ASC -> "名前順"
                                    SortOrder.NAME_DESC -> "名前順"
                                    SortOrder.CREATED_ASC -> "登録日順"
                                    SortOrder.CREATED_DESC -> "登録日順"
                                    SortOrder.CATEGORY_ASC -> "カテゴリ順"
                                    SortOrder.CATEGORY_DESC -> "カテゴリ順"
                                },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    ) {
                        DropdownMenuItem(
                            text = { Text("名前順") },
                            onClick = {
                                onSortOrderChange(SortOrder.NAME_ASC)
                                sortExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("カテゴリ順") },
                            onClick = {
                                onSortOrderChange(SortOrder.CATEGORY_ASC)
                                sortExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("登録日順") },
                            onClick = {
                                onSortOrderChange(SortOrder.CREATED_ASC)
                                sortExpanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun getSortOrderShortName(sortOrder: SortOrder): String =
    when (sortOrder) {
        SortOrder.NAME_ASC -> "名前順"
        SortOrder.NAME_DESC -> "名前順"
        SortOrder.CREATED_ASC -> "日時順"
        SortOrder.CREATED_DESC -> "日時順"
        SortOrder.CATEGORY_ASC -> "カテゴリ順"
        SortOrder.CATEGORY_DESC -> "カテゴリ順"
    }

private fun getCategoryDisplayName(category: ItemCategory?): String =
    when (category) {
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
