package com.segnities007.ui.card.search.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.ui.graphics.vector.ImageVector

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
 * フィルタオプション
 */
data class FilterOption<T>(
    val value: T?,
    val displayName: String,
)

