package com.segnities007.ui.bar

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * 汎用的な検索・フィルタ・ソート機能を提供するコンポーネント
 *
 * @param searchQuery 現在の検索クエリ
 * @param searchPlaceholder 検索バーのプレースホルダーテキスト
 * @param onSearchQueryChange 検索クエリ変更時のコールバック
 * @param filterConfig フィルタ設定（null の場合はフィルタボタンを非表示）
 * @param sortConfig ソート設定（null の場合はソートボタンを非表示）
 * @param modifier Modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <FilterType, SortType> SearchFilterSortBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    searchPlaceholder: String,
    onSearchQueryChange: (String) -> Unit,
    filterConfig: FilterConfig<FilterType>? = null,
    sortConfig: SortConfig<SortType>? = null,
) {
    val focusManager = LocalFocusManager.current
    val backgroundAlpha = 0.7f
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(backgroundAlpha),
        ),
    ) {
        Column(
            modifier = Modifier
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
                        text = searchPlaceholder,
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = backgroundAlpha),
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    }
                ),
            )

            // フィルタ・ソート行（どちらかが存在する場合のみ表示）
            if (filterConfig != null || sortConfig != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // フィルタボタン
                    filterConfig?.let { config ->
                        var filterExpanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            OutlinedButton(
                                onClick = { filterExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(backgroundAlpha),
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        ),
                                    ),
                                ),
                            ) {
                                Icon(
                                    imageVector = config.icon,
                                    contentDescription = config.iconDescription,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = config.getDisplayName(config.selectedValue),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                )
                            }

                            DropdownMenu(
                                expanded = filterExpanded,
                                onDismissRequest = { filterExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            ) {
                                config.options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(config.getDisplayName(option.value)) },
                                        onClick = {
                                            config.onValueChange(option.value)
                                            filterExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // ソートボタン
                    sortConfig?.let { config ->
                        var sortExpanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            OutlinedButton(
                                onClick = { sortExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(backgroundAlpha),
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        ),
                                    ),
                                ),
                            ) {
                                Icon(
                                    imageVector = config.icon,
                                    contentDescription = config.iconDescription,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = config.getDisplayName(config.selectedValue),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                )
                            }

                            DropdownMenu(
                                expanded = sortExpanded,
                                onDismissRequest = { sortExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            ) {
                                config.options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(config.getDisplayName(option.value)) },
                                        onClick = {
                                            config.onValueChange(option.value)
                                            sortExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * フィルタ設定用のデータクラス
 */
data class FilterConfig<T>(
    val selectedValue: T?,
    val options: List<FilterOption<T>>,
    val getDisplayName: (T?) -> String,
    val onValueChange: (T?) -> Unit,
    val icon: ImageVector = Icons.Default.FilterList,
    val iconDescription: String = "フィルタ",
)

/**
 * ソート設定用のデータクラス
 */
data class SortConfig<T>(
    val selectedValue: T,
    val options: List<SortOption<T>>,
    val getDisplayName: (T) -> String,
    val onValueChange: (T) -> Unit,
    val icon: ImageVector = Icons.AutoMirrored.Filled.Sort,
    val iconDescription: String = "ソート",
)

/**
 * フィルタオプション
 */
data class FilterOption<T>(
    val value: T?,
    val displayName: String,
)

/**
 * ソートオプション
 */
data class SortOption<T>(
    val value: T,
    val displayName: String,
)