package com.segnities007.templates.utils

import kotlinx.datetime.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.templates.mvi.SortOrder
import com.segnities007.templates.mvi.TemplateSortOrder
import kotlin.time.ExperimentalTime

/**
 * テンプレートとアイテムのフィルタリング・ソートロジックを集約するヘルパークラス
 * ViewModelの肥大化を防ぎ、単一責任の原則に従う
 */
@OptIn(ExperimentalTime::class)
object TemplateFilter {
    /**
     * アイテムにフィルタとソートを適用
     */
    fun applyItemFilters(
        allItems: List<Item>,
        searchQuery: String,
        selectedCategory: ItemCategory?,
        sortOrder: SortOrder,
    ): List<Item> {
        var filtered = allItems

        // 検索フィルタ
        if (searchQuery.isNotBlank()) {
            filtered =
                filtered.filter { item ->
                    item.name.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true)
                }
        }

        // カテゴリフィルタ
        if (selectedCategory != null) {
            filtered = filtered.filter { it.category == selectedCategory }
        }

        // ソート
        return when (sortOrder) {
            SortOrder.NAME_ASC -> filtered.sortedBy { it.name }
            SortOrder.NAME_DESC -> filtered.sortedByDescending { it.name }
            SortOrder.CREATED_ASC -> filtered.sortedBy { it.createdAt }
            SortOrder.CREATED_DESC -> filtered.sortedByDescending { it.createdAt }
            SortOrder.CATEGORY_ASC -> filtered.sortedBy { it.category.name }
            SortOrder.CATEGORY_DESC -> filtered.sortedByDescending { it.category.name }
        }
    }

    /**
     * テンプレートにフィルタとソートを適用
     */
    fun applyTemplateFilters(
        allTemplates: List<WeeklyTemplate>,
        searchQuery: String,
        selectedDayOfWeek: DayOfWeek?,
        sortOrder: TemplateSortOrder,
    ): List<WeeklyTemplate> {
        var filtered = allTemplates

        // 検索フィルタ
        if (searchQuery.isNotBlank()) {
            filtered =
                filtered.filter { template ->
                    template.title.contains(searchQuery, ignoreCase = true)
                }
        }

        // 曜日フィルタ
        if (selectedDayOfWeek != null) {
            filtered = filtered.filter { it.daysOfWeek.contains(selectedDayOfWeek) }
        }

        // ソート
        return when (sortOrder) {
            TemplateSortOrder.NAME_ASC -> filtered.sortedBy { it.title }
            TemplateSortOrder.NAME_DESC -> filtered.sortedByDescending { it.title }
            TemplateSortOrder.ITEM_COUNT_ASC -> filtered.sortedBy { it.itemIds.size }
            TemplateSortOrder.ITEM_COUNT_DESC -> filtered.sortedByDescending { it.itemIds.size }
        }
    }
}
