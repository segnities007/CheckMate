package com.segnities007.templates.page

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.ui.bar.FloatingConfirmBar
import com.segnities007.ui.card.ItemCard
import kotlin.time.ExperimentalTime

import com.segnities007.ui.scaffold.CheckMateScaffold
import com.segnities007.ui.theme.checkMateBackgroundBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateItemSelectPage(
    template: WeeklyTemplate,
    allItems: List<Item>,
    sendIntent: (TemplatesIntent) -> Unit,
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

    CheckMateScaffold(
        bottomBar = {
            FloatingConfirmBar(
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
                alpha = alpha,
            )
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.checkMateBackgroundBrush)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            TemplateItemSelectorUi(
                template = template,
                allItems = allItems,
                selectedStates = selectedStates,
            )
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        }
    }
}

@Composable
private fun TemplateItemSelectorUi(
    template: WeeklyTemplate,
    allItems: List<Item>,
    selectedStates: MutableMap<Int, Boolean>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // テンプレート情報
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
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
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = template.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // 曜日タグ
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    template.daysOfWeek.forEach { dayOfWeek ->
                        Box(
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = getDayOfWeekDisplayName(dayOfWeek),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }

        // アイテム選択リスト
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "アイテムを選択",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            allItems.forEach { item ->
                val isSelected = selectedStates[item.id] ?: false
                ItemCard(
                    item = item,
                    isChecked = isSelected,
                    onCardClick = {
                        selectedStates[item.id] = !isSelected
                    }
                ) {
                    // Selection indicator
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
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String =
    when (dayOfWeek) {
        DayOfWeek.MONDAY -> "月"
        DayOfWeek.TUESDAY -> "火"
        DayOfWeek.WEDNESDAY -> "水"
        DayOfWeek.THURSDAY -> "木"
        DayOfWeek.FRIDAY -> "金"
        DayOfWeek.SATURDAY -> "土"
        DayOfWeek.SUNDAY -> "日"
    }

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun WeeklyTemplateSelectorPreview() {
    val dummyTemplate =
        WeeklyTemplate(
            id = 1,
            title = "月曜日の忘れ物",
            itemIds = listOf(1, 2, 3),
        )
    val dummyItems = listOf<Item>()

    TemplateItemSelectPage(
        template = dummyTemplate,
        allItems = dummyItems,
        sendIntent = {},
    )
}
