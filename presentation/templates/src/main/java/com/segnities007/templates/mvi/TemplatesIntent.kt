// TemplatesIntent.kt
package com.segnities007.templates.mvi

import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.mvi.MviIntent

sealed interface TemplatesIntent : MviIntent {
    data class AddWeeklyTemplate(
        val title: String,
        val daysOfWeek: Set<DayOfWeek>,
    ) : TemplatesIntent

    data class EditWeeklyTemplate(
        val weeklyTemplate: WeeklyTemplate,
    ) : TemplatesIntent

    data class DeleteWeeklyTemplate(
        val weeklyTemplate: WeeklyTemplate,
    ) : TemplatesIntent

    data class SelectTemplate(
        val weeklyTemplate: WeeklyTemplate,
    ) : TemplatesIntent

    data object GetAllWeeklyTemplates : TemplatesIntent

    data object GetAllItems : TemplatesIntent

    data object ShowBottomSheet : TemplatesIntent

    data object HideBottomSheet : TemplatesIntent

    data object NavigateToWeeklyTemplateList : TemplatesIntent

    data object NavigateToWeeklyTemplateSelector : TemplatesIntent

    data class UpdateSearchQuery(
        val query: String,
    ) : TemplatesIntent

    data class UpdateSelectedCategory(
        val category: ItemCategory?,
    ) : TemplatesIntent

    data class UpdateSortOrder(
        val sortOrder: SortOrder,
    ) : TemplatesIntent

    // テンプレート用の検索・ソート
    data class UpdateTemplateSearchQuery(
        val query: String,
    ) : TemplatesIntent

    data class UpdateTemplateSortOrder(
        val sortOrder: TemplateSortOrder,
    ) : TemplatesIntent

    data class UpdateSelectedDayOfWeek(
        val dayOfWeek: DayOfWeek?,
    ) : TemplatesIntent
}
