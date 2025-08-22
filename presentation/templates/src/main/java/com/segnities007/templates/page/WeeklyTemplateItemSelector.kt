package com.segnities007.templates.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.templates.mvi.SortOrder
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.ui.bar.ConfirmBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyTemplateSelector(
    template: WeeklyTemplate,
    allItems: List<Item>,
    innerPadding: PaddingValues,
    sendIntent: (TemplatesIntent) -> Unit,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
) {
    val selectedStates =
        remember {
            mutableStateMapOf<Int, Boolean>().apply {
                allItems.forEach { item ->
                    put(item.id, template.itemIds.contains(item.id))
                }
            }
        }

    LaunchedEffect(Unit) {
        setFab {}
        setTopBar {}
        setNavigationBar {
            ConfirmBar(
                onConfirm = {
                    sendIntent(
                        TemplatesIntent.EditWeeklyTemplate(
                            template.copy(
                                itemIds = selectedStates.filterValues { it }.keys.toList(),
                            ),
                        ),
                    )
                },
                onCancel = {
                    sendIntent(TemplatesIntent.NavigateToWeeklyTemplateList)
                },
            )
        }
    }

    WeeklyTemplateSelectorContent(
        innerPadding = innerPadding,
        allItems = allItems,
        selectedStates = selectedStates,
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun WeeklyTemplateSelectorContent(
    innerPadding: PaddingValues,
    selectedStates: MutableMap<Int, Boolean>,
    allItems: List<Item>,
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }
    var sortOrder by remember { mutableStateOf(SortOrder.NAME_ASC) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 検索・フィルタ・ソートバー
            SearchFilterBar(
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                sortOrder = sortOrder,
                onSearchQueryChange = { query ->
                    searchQuery = query
                },
                onCategoryChange = { category ->
                    selectedCategory = category
                },
                onSortOrderChange = { order ->
                    sortOrder = order
                },
            )

            HorizontalDividerWithLabel("登録・未登録のアイテム")

            // フィルタリングされたアイテムリスト
            val filteredItems = allItems.filter { item ->
                val matchesSearch = searchQuery.isEmpty() || 
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == null || item.category == selectedCategory
                matchesSearch && matchesCategory
            }.let { items ->
                when (sortOrder) {
                    SortOrder.NAME_ASC -> items.sortedBy { it.name }
                    SortOrder.NAME_DESC -> items.sortedByDescending { it.name }
                    SortOrder.CREATED_ASC -> items.sortedBy { it.createdAt }
                    SortOrder.CREATED_DESC -> items.sortedByDescending { it.createdAt }
                    SortOrder.CATEGORY_ASC -> items.sortedBy { it.category.name }
                    SortOrder.CATEGORY_DESC -> items.sortedByDescending { it.category.name }
                }
            }

            ItemsList(
                items = filteredItems,
                selectedStates = selectedStates,
                onItemClick = { item ->
                    // アイテムの選択状態を切り替え
                    val currentState = selectedStates[item.id] ?: false
                    selectedStates[item.id] = !currentState
                },
            )
        }
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchFilterBar(
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                modifier = Modifier.weight(1f),
            ) {
                OutlinedButton(
                    onClick = { showCategoryMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.verticalGradient(
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
                modifier = Modifier.weight(1f),
            ) {
                OutlinedButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.verticalGradient(
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

@Composable
private fun ItemsList(
    items: List<Item>,
    selectedStates: MutableMap<Int, Boolean>,
    onItemClick: (Item) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            val isSelected = selectedStates[item.id] ?: false
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // 左側: アイコンまたは画像
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (item.imagePath.isNotEmpty()) {
                            AsyncImage(
                                model = item.imagePath,
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Inventory,
                                    contentDescription = "Item Icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                        }
                    }

                    // 中央: テキスト情報
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 24.sp
                        )

                        Text(
                            text = getCategoryDisplayName(item.category),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp
                        )
                    }

                    // 右側: チェックアイコン
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp),
                        )
                    }
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

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun WeeklyTemplateSelectorContentPreview() {
    val dummyItems =
        listOf(
            Item(id = 1, name = "歯ブラシ", description = "旅行用", imagePath = "https://placehold.co/100x100"),
            Item(id = 2, name = "ノート", description = "大学の授業用", imagePath = "https://placehold.co/100x100"),
            Item(id = 3, name = "充電器", description = "スマホ用", imagePath = ""),
            Item(id = 4, name = "マスク", description = "予備", imagePath = ""),
        )
    val dummyTemplate =
        WeeklyTemplate(
            id = 1,
            title = "旅行テンプレート",
            itemIds = listOf(1, 2),
        )

    MaterialTheme {
        WeeklyTemplateSelectorContent(
            allItems = dummyItems,
            innerPadding = PaddingValues(0.dp),
            selectedStates = remember {
                mutableStateMapOf<Int, Boolean>().apply {
                    dummyTemplate.itemIds.forEach { put(it, true) }
                }
            },
        )
    }
}
