package com.segnities007.templates.page

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.model.DayOfWeek
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.ui.bar.ConfirmBar
import kotlin.collections.set
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSelector(
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

    val scrollState = rememberScrollState()
    val alpha by remember {
        derivedStateOf {
            (1f - scrollState.value / 100f).coerceIn(0f, 1f)
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
                alpha = alpha
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
    ){
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        TemplateItemSelectorUi(
            template = template,
            allItems = allItems,
            selectedStates = selectedStates,
        )
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }

}

@Composable
private fun TemplateItemSelectorUi(
    template: WeeklyTemplate,
    allItems: List<Item>,
    selectedStates: MutableMap<Int, Boolean>
){
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // テンプレート情報
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = template.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 曜日タグ
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    template.daysOfWeek.forEach { dayOfWeek ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
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

        // アイテム選択リスト
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "アイテムを選択",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            allItems.forEach { item ->
                val isSelected = selectedStates[item.id] ?: false
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 1.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    ),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    onClick = {
                        selectedStates[item.id] = !isSelected
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // アイテム画像
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            getCategoryColor(item.category).copy(alpha = 0.1f),
                                            getCategoryColor(item.category).copy(alpha = 0.05f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.imagePath.isNotEmpty()) {
                                AsyncImage(
                                    model = item.imagePath,
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Inventory,
                                    contentDescription = "Item Icon",
                                    tint = getCategoryColor(item.category),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        // アイテム情報
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            CategoryTag(category = item.category)
                        }

                        // 選択状態インジケーター
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
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

private fun getCategoryColor(category: ItemCategory): Color {
    return when (category) {
        ItemCategory.STUDY_SUPPLIES -> Color(0xFF2196F3) // Blue - 学業用品
        ItemCategory.DAILY_SUPPLIES -> Color(0xFF4CAF50) // Green - 生活用品
        ItemCategory.CLOTHING_SUPPLIES -> Color(0xFF9C27B0) // Purple - 衣類用品
        ItemCategory.FOOD_SUPPLIES -> Color(0xFFFF9800) // Orange - 食事用品
        ItemCategory.HEALTH_SUPPLIES -> Color(0xFFF44336) // Red - 健康用品
        ItemCategory.BEAUTY_SUPPLIES -> Color(0xFFE91E63) // Pink - 美容用品
        ItemCategory.EVENT_SUPPLIES -> Color(0xFF673AB7) // Deep Purple - イベント用品
        ItemCategory.HOBBY_SUPPLIES -> Color(0xFF3F51B5) // Indigo - 趣味用品
        ItemCategory.TRANSPORT_SUPPLIES -> Color(0xFF009688) // Teal - 交通用品
        ItemCategory.CHARGING_SUPPLIES -> Color(0xFF795548) // Brown - 充電用品
        ItemCategory.WEATHER_SUPPLIES -> Color(0xFF607D8B) // Blue Grey - 天候対策用品
        ItemCategory.ID_SUPPLIES -> Color(0xFF8BC34A) // Light Green - 証明用品
        ItemCategory.OTHER_SUPPLIES -> Color(0xFF607D8B) // Blue Grey - その他用品
    }
}

@Composable
private fun CategoryTag(
    category: ItemCategory
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(getCategoryColor(category).copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = getCategoryDisplayName(category),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = getCategoryColor(category),
            fontSize = 12.sp
        )
    }
}

private fun getCategoryDisplayName(category: ItemCategory): String {
    return when (category) {
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
fun WeeklyTemplateSelectorPreview() {
    val dummyTemplate = WeeklyTemplate(
        id = 1,
        title = "月曜日の忘れ物",
        itemIds = listOf(1, 2, 3),
    )
    val dummyItems = listOf<Item>()

    TemplateSelector(
        template = dummyTemplate,
        allItems = dummyItems,
        innerPadding = PaddingValues(0.dp),
        sendIntent = {},
        setFab = {},
        setTopBar = {},
        setNavigationBar = {},
    )
}

