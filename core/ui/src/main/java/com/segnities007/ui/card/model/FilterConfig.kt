package com.segnities007.ui.card.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.ui.graphics.vector.ImageVector
import com.segnities007.ui.card.FilterOption

data class FilterConfig<T>(
    val selectedValue: T?,
    val options: List<FilterOption<T>>,
    val getDisplayName: (T?) -> String,
    val onValueChange: (T?) -> Unit,
    val icon: ImageVector = Icons.Default.FilterList,
    val iconDescription: String = "フィルタ",
)

data class FilterOption<T>(
    val value: T?,
    val displayName: String,
)