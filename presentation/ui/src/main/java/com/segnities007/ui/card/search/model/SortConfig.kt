package com.segnities007.ui.card.search.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.ui.graphics.vector.ImageVector

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
 * ソートオプション
 */
data class SortOption<T>(
    val value: T,
    val displayName: String,
)