package com.segnities007.templates.mvi

import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.navigation.NavKey
import com.segnities007.ui.mvi.MviState

data class TemplatesState(
    val weeklyTemplates: List<WeeklyTemplate> = emptyList(),
    val isLoadingTemplates: Boolean = false,
    val isShowingBottomSheet: Boolean = false,
    val selectedTemplate: WeeklyTemplate? = null, // null で「未選択」を表現
    val allItems: List<Item> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: ItemCategory? = null,
    val sortOrder: SortOrder = SortOrder.NAME_ASC,
    val filteredItems: List<Item> = emptyList(),
    // テンプレート用の検索・ソート
    val templateSearchQuery: String = "",
    val templateSortOrder: TemplateSortOrder = TemplateSortOrder.NAME_ASC,
    val selectedDayOfWeek: DayOfWeek? = null,
    val filteredTemplates: List<WeeklyTemplate> = emptyList(),
    // 削除確認ダイアログ
    val templateToDelete: WeeklyTemplate? = null,
    // ICS インポート状態
    val isImportingIcs: Boolean = false,
    val currentRoute: NavKey = NavKey.WeeklyTemplateList,
) : MviState

enum class SortOrder {
    NAME_ASC,
    NAME_DESC,
    CREATED_ASC,
    CREATED_DESC,
    CATEGORY_ASC,
    CATEGORY_DESC,
}

enum class TemplateSortOrder {
    NAME_ASC,
    NAME_DESC,
    ITEM_COUNT_ASC,
    ITEM_COUNT_DESC,
}
