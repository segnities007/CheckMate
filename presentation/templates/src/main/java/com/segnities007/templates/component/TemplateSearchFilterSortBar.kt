package com.segnities007.templates.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults.elevatedCardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.segnities007.model.DayOfWeek
import com.segnities007.templates.mvi.TemplateSortOrder
import com.segnities007.templates.utils.getDayOfWeekDisplayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSearchFilterSortBar(
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
        colors =
            elevatedCardColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
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
                        text = "テンプレートを検索...",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "検索",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    ),
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                singleLine = true,
            )

            // フィルタ・ソート行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // 曜日フィルタ
                var dayExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedButton(
                        onClick = { dayExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            ),
                        border = ButtonDefaults.outlinedButtonBorder(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "曜日",
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedDayOfWeek?.let { getDayOfWeekDisplayName(it) } ?: "全曜日",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    DropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false },
                        modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surface),
                    ) {
                        DropdownMenuItem(
                            text = { Text("全曜日") },
                            onClick = {
                                onDayOfWeekChange(null)
                                dayExpanded = false
                            },
                        )
                        DayOfWeek.entries.forEach { dayOfWeek ->
                            DropdownMenuItem(
                                text = { Text(getDayOfWeekDisplayName(dayOfWeek)) },
                                onClick = {
                                    onDayOfWeekChange(dayOfWeek)
                                    dayExpanded = false
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
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            ),
                        border =
                            ButtonDefaults.outlinedButtonBorder.copy(
                                brush =
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors =
                                            listOf(
                                                androidx.compose.material3.MaterialTheme.colorScheme.primary
                                                    .copy(alpha = 0.3f),
                                                androidx.compose.material3.MaterialTheme.colorScheme.primary
                                                    .copy(alpha = 0.1f),
                                            ),
                                    ),
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "ソート",
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text =
                                when (sortOrder) {
                                    TemplateSortOrder.NAME_ASC -> "名前順"
                                    TemplateSortOrder.NAME_DESC -> "名前順"
                                    TemplateSortOrder.ITEM_COUNT_ASC -> "アイテム数順"
                                    TemplateSortOrder.ITEM_COUNT_DESC -> "アイテム数順"
                                },
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false },
                        modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surface),
                    ) {
                        DropdownMenuItem(
                            text = { Text("名前順") },
                            onClick = {
                                onSortOrderChange(TemplateSortOrder.NAME_ASC)
                                sortExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("アイテム数順") },
                            onClick = {
                                onSortOrderChange(TemplateSortOrder.ITEM_COUNT_ASC)
                                sortExpanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}
