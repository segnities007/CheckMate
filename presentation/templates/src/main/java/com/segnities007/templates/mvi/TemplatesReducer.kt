package com.segnities007.templates.mvi

import com.segnities007.ui.mvi.MviReducer

class TemplatesReducer : MviReducer<TemplatesState, TemplatesIntent> {
    override fun reduce(
        currentState: TemplatesState,
        intent: TemplatesIntent,
    ): TemplatesState =
        when (intent) {
            TemplatesIntent.ShowBottomSheet -> currentState.copy(isShowingBottomSheet = true)
            TemplatesIntent.HideBottomSheet -> currentState.copy(isShowingBottomSheet = false)
            is TemplatesIntent.UpdateSearchQuery -> currentState.copy(searchQuery = intent.query)
            is TemplatesIntent.UpdateSelectedCategory -> currentState.copy(selectedCategory = intent.category)
            is TemplatesIntent.UpdateSortOrder -> currentState.copy(sortOrder = intent.sortOrder)
            is TemplatesIntent.UpdateTemplateSearchQuery -> currentState.copy(templateSearchQuery = intent.query)
            is TemplatesIntent.UpdateTemplateSortOrder -> currentState.copy(templateSortOrder = intent.sortOrder)
            is TemplatesIntent.UpdateSelectedDayOfWeek -> currentState.copy(selectedDayOfWeek = intent.dayOfWeek)
            is TemplatesIntent.SelectTemplate -> currentState.copy(selectedTemplate = intent.weeklyTemplate)
            is TemplatesIntent.DeleteWeeklyTemplate -> currentState.copy(templateToDelete = intent.weeklyTemplate)
            TemplatesIntent.ConfirmDeleteTemplate, TemplatesIntent.CancelDeleteTemplate -> currentState.copy(templateToDelete = null)
            is TemplatesIntent.SetFilteredItems -> currentState.copy(filteredItems = intent.filteredItems)
            is TemplatesIntent.SetFilteredTemplates -> currentState.copy(filteredTemplates = intent.filteredTemplates)
            is TemplatesIntent.SetAllItems -> currentState.copy(allItems = intent.allItems)
            is TemplatesIntent.SetWeeklyTemplates -> currentState.copy(weeklyTemplates = intent.weeklyTemplates)
            else -> currentState
        }
}
