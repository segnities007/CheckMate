package com.segnities007.templates.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults.elevatedCardColors
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.navigation.HubRoute
import com.segnities007.templates.mvi.TemplateSortOrder
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel

@Composable
fun WeeklyTemplateList(
    innerPadding: PaddingValues,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
    sendIntent: (TemplatesIntent) -> Unit,
    templates: List<WeeklyTemplate>,
    allItems: List<Item>,
    templateSearchQuery: String,
    templateSortOrder: TemplateSortOrder,
    selectedDayOfWeek: DayOfWeek?,
    onTemplateClick: (WeeklyTemplate) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSortOrderChange: (TemplateSortOrder) -> Unit,
    onDayOfWeekChange: (DayOfWeek?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val targetAlpha by remember {
        derivedStateOf {
            val firstVisibleItemIndex = listState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            
            // より安定した透明度計算
            when {
                firstVisibleItemIndex > 0 -> 0f // スクロール開始時は非表示
                firstVisibleItemScrollOffset > 50 -> 0f // 50px以上スクロールしたら非表示
                else -> (1f - firstVisibleItemScrollOffset / 50f).coerceIn(0f, 1f) // 0-50pxの範囲でフェード
            }
        }
    }
    
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 200),
        label = "navigationBarAlpha"
    )

    LaunchedEffect(Unit) {
        setNavigationBar {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = HubRoute.Templates,
                onNavigate = onNavigate,
            )
        }
        setFab {
            if (alpha > 0f) {
                FloatingActionButton(
                    onClick = { sendIntent(TemplatesIntent.ShowBottomSheet) },
                    containerColor = FloatingActionButtonDefaults.containerColor.copy(alpha = alpha),
                    contentColor = contentColorFor(FloatingActionButtonDefaults.containerColor).copy(alpha = alpha),
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Add Template",
                    )
                }
            }
        }
        setTopBar {}
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateTopPadding()
        )
    ) {
        item {
            // 検索・フィルタ・ソートバー
            TemplateSearchFilterSortBar(
                searchQuery = templateSearchQuery,
                sortOrder = templateSortOrder,
                selectedDayOfWeek = selectedDayOfWeek,
                onSearchQueryChange = onSearchQueryChange,
                onSortOrderChange = onSortOrderChange,
                onDayOfWeekChange = onDayOfWeekChange
            )
        }
        item {
            HorizontalDividerWithLabel(
                label = "テンプレート一覧"
            )
        }

        // テンプレートリスト
        items(templates) { template ->
            TemplateCard(
                template = template,
                allItems = allItems,
                onClick = { onTemplateClick(template) },
                onDelete = { sendIntent(TemplatesIntent.DeleteWeeklyTemplate(template)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateSearchFilterSortBar(
    searchQuery: String,
    sortOrder: TemplateSortOrder,
    selectedDayOfWeek: DayOfWeek?,
    onSearchQueryChange: (String) -> Unit,
    onSortOrderChange: (TemplateSortOrder) -> Unit,
    onDayOfWeekChange: (DayOfWeek?) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = elevatedCardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 2.dp,
            focusedElevation = 1.dp,
            hoveredElevation = 1.dp
        ),
        colors = elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 検索バー
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "テンプレートを検索...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "検索",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true
            )

            // フィルタ・ソート行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 曜日フィルタ
                var dayExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedButton(
                        onClick = { dayExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
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
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "曜日",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedDayOfWeek?.let { getDayOfWeekDisplayName(it) } ?: "全曜日",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("全曜日") },
                            onClick = {
                                onDayOfWeekChange(null)
                                dayExpanded = false
                            }
                        )
                        DayOfWeek.values().forEach { dayOfWeek ->
                            DropdownMenuItem(
                                text = { Text(getDayOfWeekDisplayName(dayOfWeek)) },
                                onClick = {
                                    onDayOfWeekChange(dayOfWeek)
                                    dayExpanded = false
                                }
                            )
                        }
                    }
                }

                // ソート
                var sortExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedButton(
                        onClick = { sortExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
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
                            imageVector = Icons.Default.Sort,
                            contentDescription = "ソート",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (sortOrder) {
                                TemplateSortOrder.NAME_ASC -> "名前順"
                                TemplateSortOrder.NAME_DESC -> "名前順"
                                TemplateSortOrder.ITEM_COUNT_ASC -> "アイテム数順"
                                TemplateSortOrder.ITEM_COUNT_DESC -> "アイテム数順"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("名前順") },
                            onClick = {
                                onSortOrderChange(TemplateSortOrder.NAME_ASC)
                                sortExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("アイテム数順") },
                            onClick = {
                                onSortOrderChange(TemplateSortOrder.ITEM_COUNT_ASC)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: WeeklyTemplate,
    allItems: List<Item>,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // テンプレートのアイテムIDに対応するアイテムを取得
    val templateItems = allItems.filter { item -> template.itemIds.contains(item.id) }
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            elevation = elevatedCardElevation(
                defaultElevation = 1.dp,
                pressedElevation = 2.dp,
                focusedElevation = 1.dp,
                hoveredElevation = 1.dp
            ),
            colors = elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // メインコンテンツ
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ヘッダー
                    Text(
                        text = template.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // 曜日タグ
                    if (template.daysOfWeek.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(template.daysOfWeek.toList()) { dayOfWeek ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = getDayOfWeekDisplayName(dayOfWeek),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                
                // 削除ボタン
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "削除",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        // インデックス付箋風のアイテム数表示（右上）
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-16).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${template.itemIds.size}アイテム",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

private fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "月"
        DayOfWeek.TUESDAY -> "火"
        DayOfWeek.WEDNESDAY -> "水"
        DayOfWeek.THURSDAY -> "木"
        DayOfWeek.FRIDAY -> "金"
        DayOfWeek.SATURDAY -> "土"
        DayOfWeek.SUNDAY -> "日"
    }
}

private fun getTemplateSortOrderShortName(sortOrder: TemplateSortOrder): String {
    return when (sortOrder) {
        TemplateSortOrder.NAME_ASC -> "名前順"
        TemplateSortOrder.NAME_DESC -> "名前順"
        TemplateSortOrder.ITEM_COUNT_ASC -> "アイテム数順"
        TemplateSortOrder.ITEM_COUNT_DESC -> "アイテム数順"
    }
}

@Preview(showBackground = true)
@Composable
fun WeeklyTemplateListPreview() {
    val dummyTemplates =
        listOf(
            WeeklyTemplate(
                id = 1,
                title = "月曜日の忘れ物",
                itemIds = listOf(1, 2, 3),
            ),
            WeeklyTemplate(
                id = 2,
                title = "火曜日の忘れ物",
                itemIds = listOf(1, 2, 3),
            ),
        )

    WeeklyTemplateList(
        innerPadding = PaddingValues(0.dp),
        setFab = {},
        setTopBar = {},
        setNavigationBar = {},
        onNavigate = {},
        sendIntent = {},
        templates = dummyTemplates,
        allItems = listOf(),
        templateSearchQuery = "",
        templateSortOrder = TemplateSortOrder.NAME_ASC,
        selectedDayOfWeek = null,
        onTemplateClick = {},
        onSearchQueryChange = {},
        onSortOrderChange = {},
        onDayOfWeekChange = {},
        modifier = Modifier.fillMaxWidth(),
    )
}
