// TemplatesViewModel.kt
package com.segnities007.templates.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TemplatesViewModel(
    private val weeklyTemplateRepository: WeeklyTemplateRepository,
    private val itemRepository: ItemRepository,
) : BaseViewModel<TemplatesIntent, TemplatesState, TemplatesEffect>(
        initialState = TemplatesState(),
    ) {
    init {
        // 初期ロード
        viewModelScope.launch { handleIntent(TemplatesIntent.GetAllWeeklyTemplates) }
        viewModelScope.launch { handleIntent(TemplatesIntent.GetAllItems) }
    }

    override suspend fun handleIntent(intent: TemplatesIntent) {
        when (intent) {
            is TemplatesIntent.AddWeeklyTemplate ->
                addWeeklyTemplate(
                    title = intent.title,
                    daysOfWeek = intent.daysOfWeek,
                )

            is TemplatesIntent.EditWeeklyTemplate -> editWeeklyTemplate(intent.weeklyTemplate)
            is TemplatesIntent.DeleteWeeklyTemplate -> deleteWeeklyTemplate(intent.weeklyTemplate)
            is TemplatesIntent.SelectTemplate -> selectTemplate(intent.weeklyTemplate)

            TemplatesIntent.GetAllWeeklyTemplates -> getAllWeeklyTemplates()
            TemplatesIntent.GetAllItems -> getAllItems()

            TemplatesIntent.ShowBottomSheet -> setState { copy(isShowingBottomSheet = true) }
            TemplatesIntent.HideBottomSheet -> setState { copy(isShowingBottomSheet = false) }

            TemplatesIntent.NavigateToWeeklyTemplateList ->
                sendEffect { TemplatesEffect.NavigateToWeeklyTemplateList }
            TemplatesIntent.NavigateToWeeklyTemplateSelector ->
                sendEffect { TemplatesEffect.NavigateToWeeklyTemplateSelector }

            is TemplatesIntent.UpdateSearchQuery -> updateSearchQuery(intent)
            is TemplatesIntent.UpdateSelectedCategory -> updateSelectedCategory(intent)
            is TemplatesIntent.UpdateSortOrder -> updateSortOrder(intent)
            is TemplatesIntent.UpdateTemplateSearchQuery -> updateTemplateSearchQuery(intent)
            is TemplatesIntent.UpdateTemplateSortOrder -> updateTemplateSortOrder(intent)
            is TemplatesIntent.UpdateSelectedDayOfWeek -> updateSelectedDayOfWeek(intent)
        }
    }

    private fun updateSearchQuery(intent: TemplatesIntent.UpdateSearchQuery) {
        setState { copy(searchQuery = intent.query) }
        applyFilters()
    }

    private fun updateSelectedCategory(intent: TemplatesIntent.UpdateSelectedCategory) {
        setState { copy(selectedCategory = intent.category) }
        applyFilters()
    }

    private fun updateSortOrder(intent: TemplatesIntent.UpdateSortOrder) {
        setState { copy(sortOrder = intent.sortOrder) }
        applyFilters()
    }

    private fun updateTemplateSearchQuery(intent: TemplatesIntent.UpdateTemplateSearchQuery) {
        setState { copy(templateSearchQuery = intent.query) }
        applyTemplateFilters()
    }

    private fun updateTemplateSortOrder(intent: TemplatesIntent.UpdateTemplateSortOrder) {
        setState { copy(templateSortOrder = intent.sortOrder) }
        applyTemplateFilters()
    }

    private fun updateSelectedDayOfWeek(intent: TemplatesIntent.UpdateSelectedDayOfWeek) {
        setState { copy(selectedDayOfWeek = intent.dayOfWeek) }
        applyTemplateFilters()
    }

    @OptIn(ExperimentalTime::class)
    private fun applyFilters() {
        val currentState = state.value
        var filteredItems = currentState.allItems

        // 検索フィルタ
        if (currentState.searchQuery.isNotBlank()) {
            filteredItems = filteredItems.filter { item ->
                item.name.contains(currentState.searchQuery, ignoreCase = true) ||
                item.description.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        // カテゴリフィルタ
        if (currentState.selectedCategory != null) {
            filteredItems = filteredItems.filter { item ->
                item.category == currentState.selectedCategory
            }
        }

        // 並び替え
        filteredItems = when (currentState.sortOrder) {
            SortOrder.NAME_ASC -> filteredItems.sortedBy { it.name }
            SortOrder.NAME_DESC -> filteredItems.sortedByDescending { it.name }
            SortOrder.CREATED_ASC -> filteredItems.sortedBy { it.createdAt }
            SortOrder.CREATED_DESC -> filteredItems.sortedByDescending { it.createdAt }
            SortOrder.CATEGORY_ASC -> filteredItems.sortedBy { it.category.name }
            SortOrder.CATEGORY_DESC -> filteredItems.sortedByDescending { it.category.name }
        }

        setState { copy(filteredItems = filteredItems) }
    }

    private fun applyTemplateFilters() {
        val currentState = state.value
        var filteredTemplates = currentState.weeklyTemplates

        // 検索フィルタ
        if (currentState.templateSearchQuery.isNotBlank()) {
            filteredTemplates = filteredTemplates.filter { template ->
                template.title.contains(currentState.templateSearchQuery, ignoreCase = true)
            }
        }

        // 曜日フィルタ
        if (currentState.selectedDayOfWeek != null) {
            filteredTemplates = filteredTemplates.filter { template ->
                template.daysOfWeek.contains(currentState.selectedDayOfWeek)
            }
        }

        // 並び替え
        filteredTemplates = when (currentState.templateSortOrder) {
            TemplateSortOrder.NAME_ASC -> filteredTemplates.sortedBy { it.title }
            TemplateSortOrder.NAME_DESC -> filteredTemplates.sortedByDescending { it.title }
            TemplateSortOrder.ITEM_COUNT_ASC -> filteredTemplates.sortedBy { it.itemIds.size }
            TemplateSortOrder.ITEM_COUNT_DESC -> filteredTemplates.sortedByDescending { it.itemIds.size }
        }

        setState { copy(filteredTemplates = filteredTemplates) }
    }

    private fun getAllItems() {
        viewModelScope.launch {
            val items = itemRepository.getAllItems()
            setState { copy(allItems = items) }
            applyFilters()
        }
    }

    private fun getAllWeeklyTemplates() {
        viewModelScope.launch {
            val templates = weeklyTemplateRepository.getAllTemplates()
            setState { copy(weeklyTemplates = templates) }
            applyTemplateFilters()
        }
    }

    private fun addWeeklyTemplate(
        title: String,
        daysOfWeek: Set<com.segnities007.model.DayOfWeek>,
    ) {
        viewModelScope.launch {
            weeklyTemplateRepository.insertTemplate(
                WeeklyTemplate(
                    title = title,
                    daysOfWeek = daysOfWeek,
                    itemIds = emptyList(), // 追加時は空で作成し、後から編集で詰める
                ),
            )
            getAllWeeklyTemplates()
        }
    }

    private fun editWeeklyTemplate(template: WeeklyTemplate) {
        viewModelScope.launch {
            weeklyTemplateRepository.updateTemplate(template)
            getAllWeeklyTemplates()
            sendEffect { TemplatesEffect.NavigateToWeeklyTemplateList }
        }
    }

    private fun deleteWeeklyTemplate(template: WeeklyTemplate) {
        viewModelScope.launch {
            weeklyTemplateRepository.deleteTemplate(template)
            getAllWeeklyTemplates()
        }
    }

    private fun selectTemplate(template: WeeklyTemplate) {
        setState { copy(selectedTemplate = template) }
        sendEffect { TemplatesEffect.NavigateToWeeklyTemplateSelector }
    }
}
