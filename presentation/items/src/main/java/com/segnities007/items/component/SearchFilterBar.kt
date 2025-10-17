package com.segnities007.items.component

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.segnities007.items.mvi.SortOrder
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.card.search.SearchCard
import com.segnities007.ui.card.search.model.FilterConfig
import com.segnities007.ui.card.search.model.FilterOption
import com.segnities007.ui.card.search.model.SortConfig
import com.segnities007.ui.card.search.model.SortOption

@Composable
fun SearchFilterBar(
    searchQuery: String,
    selectedCategory: ItemCategory?,
    sortOrder: SortOrder,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (ItemCategory?) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filterConfig = FilterConfig(
        selectedValue = selectedCategory,
        options = listOf(
            FilterOption<ItemCategory?>(null, "すべて")
        ) + ItemCategory.entries.map { category ->
            FilterOption(category, getCategoryDisplayName(category))
        },
        getDisplayName = { option -> option?.let { getCategoryDisplayName(it) } ?: "カテゴリ" },
        onValueChange = onCategoryChange,
        iconDescription = "カテゴリ",
    )

    val sortConfig = SortConfig(
        selectedValue = sortOrder,
        options = listOf(
            SortOption(SortOrder.NAME_ASC, "名前順"),
            SortOption(SortOrder.CATEGORY_ASC, "カテゴリ順"),
            SortOption(SortOrder.CREATED_ASC, "登録日順"),
        ),
        getDisplayName = { getSortOrderShortName(it) },
        onValueChange = onSortOrderChange,
        iconDescription = "ソート",
    )

    SearchCard(
        searchQuery = searchQuery,
        searchPlaceholder = "アイテムを検索...",
        onSearchQueryChange = onSearchQueryChange,
        filterConfig = filterConfig,
        sortConfig = sortConfig,
        modifier = modifier,
    )
}

private fun getSortOrderShortName(sortOrder: SortOrder): String =
    when (sortOrder) {
        SortOrder.NAME_ASC -> "名前順"
        SortOrder.NAME_DESC -> "名前順"
        SortOrder.CREATED_ASC -> "日時順"
        SortOrder.CREATED_DESC -> "日時順"
        SortOrder.CATEGORY_ASC -> "カテゴリ順"
        SortOrder.CATEGORY_DESC -> "カテゴリ順"
    }

private fun getCategoryDisplayName(category: ItemCategory?): String =
    when (category) {
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
