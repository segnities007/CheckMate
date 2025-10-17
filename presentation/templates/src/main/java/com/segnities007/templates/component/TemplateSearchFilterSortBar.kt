package com.segnities007.templates.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.segnities007.model.DayOfWeek
import com.segnities007.templates.mvi.TemplateSortOrder
import com.segnities007.templates.utils.getDayOfWeekDisplayName
import com.segnities007.ui.card.FilterConfig
import com.segnities007.ui.card.FilterOption
import com.segnities007.ui.card.SearchCardWithFilter
import com.segnities007.ui.card.SortConfig
import com.segnities007.ui.card.SortOption

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
    val filterConfig = FilterConfig(
        selectedValue = selectedDayOfWeek,
        options = listOf(
            FilterOption<DayOfWeek?>(null, "全曜日")
        ) + DayOfWeek.entries.map { dayOfWeek ->
            FilterOption(dayOfWeek, getDayOfWeekDisplayName(dayOfWeek))
        },
    getDisplayName = { option -> option?.let { getDayOfWeekDisplayName(it) } ?: "全曜日" },
        onValueChange = onDayOfWeekChange,
        iconDescription = "曜日",
    )

    val sortConfig = SortConfig(
        selectedValue = sortOrder,
        options = listOf(
            SortOption(TemplateSortOrder.NAME_ASC, "名前順"),
            SortOption(TemplateSortOrder.ITEM_COUNT_ASC, "アイテム数順"),
        ),
        getDisplayName = { 
            when (it) {
                TemplateSortOrder.NAME_ASC -> "名前順"
                TemplateSortOrder.NAME_DESC -> "名前順"
                TemplateSortOrder.ITEM_COUNT_ASC -> "アイテム数順"
                TemplateSortOrder.ITEM_COUNT_DESC -> "アイテム数順"
            }
        },
        onValueChange = onSortOrderChange,
        icon = Icons.AutoMirrored.Filled.Sort,
        iconDescription = "ソート",
    )

    SearchCardWithFilter(
        searchQuery = searchQuery,
        searchPlaceholder = "テンプレートを検索...",
        onSearchQueryChange = onSearchQueryChange,
        filterConfig = filterConfig,
        sortConfig = sortConfig,
        modifier = modifier,
    )
}
