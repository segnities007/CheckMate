package com.segnities007.templates.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.IcsTemplateRepository
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TemplatesViewModel(
    private val weeklyTemplateRepository: WeeklyTemplateRepository,
    private val itemRepository: ItemRepository,
    private val icsTemplateRepository: IcsTemplateRepository,
) : BaseViewModel<TemplatesIntent, TemplatesState, TemplatesEffect>(
        initialState = TemplatesState(),
    ) {
    private val reducer: TemplatesReducer = TemplatesReducer()

    init {
        sendIntent(TemplatesIntent.GetAllWeeklyTemplates)
        sendIntent(TemplatesIntent.GetAllItems)
    }

    override suspend fun handleIntent(intent: TemplatesIntent) {
        when (intent) {
            is TemplatesIntent.AddWeeklyTemplate ->
                addWeeklyTemplate(
                    title = intent.title,
                    daysOfWeek = intent.daysOfWeek,
                )

            is TemplatesIntent.EditWeeklyTemplate -> editWeeklyTemplate(intent.weeklyTemplate)
            is TemplatesIntent.DeleteWeeklyTemplate -> showDeleteConfirmation(intent.weeklyTemplate)
            is TemplatesIntent.SelectTemplate -> selectTemplate(intent.weeklyTemplate)

            TemplatesIntent.GetAllWeeklyTemplates -> getAllWeeklyTemplates()
            TemplatesIntent.GetAllItems -> getAllItems()
            is TemplatesIntent.SetAllItems -> setState { reducer.reduce(this, intent) }
            is TemplatesIntent.SetWeeklyTemplates -> setState { reducer.reduce(this, intent) }
            is TemplatesIntent.SetFilteredItems -> setState { reducer.reduce(this, intent) }
            is TemplatesIntent.SetFilteredTemplates -> setState { reducer.reduce(this, intent) }

            TemplatesIntent.ShowBottomSheet -> setState { reducer.reduce(this, TemplatesIntent.ShowBottomSheet) }
            TemplatesIntent.HideBottomSheet -> setState { reducer.reduce(this, TemplatesIntent.HideBottomSheet) }

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

            // 削除確認ダイアログ
            TemplatesIntent.ConfirmDeleteTemplate -> confirmDeleteTemplate()
            TemplatesIntent.CancelDeleteTemplate -> cancelDeleteTemplate()

            is TemplatesIntent.ImportIcsTemplates -> importIcs(intent.uri)
            TemplatesIntent.ShowIcsFilePicker -> sendEffect { TemplatesEffect.LaunchIcsPicker }
            TemplatesIntent.ImportIcsStarted -> setState { copy(isImportingIcs = true) }
            TemplatesIntent.ImportIcsFinished -> setState { copy(isImportingIcs = false) }
        }
    }

    private fun updateSearchQuery(intent: TemplatesIntent.UpdateSearchQuery) {
        setState { reducer.reduce(this, intent) }
        applyFilters()
    }

    private fun updateSelectedCategory(intent: TemplatesIntent.UpdateSelectedCategory) {
        setState { reducer.reduce(this, intent) }
        applyFilters()
    }

    private fun updateSortOrder(intent: TemplatesIntent.UpdateSortOrder) {
        setState { reducer.reduce(this, intent) }
        applyFilters()
    }

    private fun updateTemplateSearchQuery(intent: TemplatesIntent.UpdateTemplateSearchQuery) {
        setState { reducer.reduce(this, intent) }
        applyTemplateFilters()
    }

    private fun updateTemplateSortOrder(intent: TemplatesIntent.UpdateTemplateSortOrder) {
        setState { reducer.reduce(this, intent) }
        applyTemplateFilters()
    }

    private fun updateSelectedDayOfWeek(intent: TemplatesIntent.UpdateSelectedDayOfWeek) {
        setState { reducer.reduce(this, intent) }
        applyTemplateFilters()
    }

    @OptIn(ExperimentalTime::class)
    private fun applyFilters() {
        val currentState = state.value
        var filteredItems = currentState.allItems

        // 検索フィルタ
        if (currentState.searchQuery.isNotBlank()) {
            filteredItems =
                filteredItems.filter { item ->
                    item.name.contains(currentState.searchQuery, ignoreCase = true) ||
                        item.description.contains(currentState.searchQuery, ignoreCase = true)
                }
        }

        // カテゴリフィルタ
        if (currentState.selectedCategory != null) {
            filteredItems =
                filteredItems.filter { item ->
                    item.category == currentState.selectedCategory
                }
        }

        // 並び替え
        filteredItems =
            when (currentState.sortOrder) {
                SortOrder.NAME_ASC -> filteredItems.sortedBy { it.name }
                SortOrder.NAME_DESC -> filteredItems.sortedByDescending { it.name }
                SortOrder.CREATED_ASC -> filteredItems.sortedBy { it.createdAt }
                SortOrder.CREATED_DESC -> filteredItems.sortedByDescending { it.createdAt }
                SortOrder.CATEGORY_ASC -> filteredItems.sortedBy { it.category.name }
                SortOrder.CATEGORY_DESC -> filteredItems.sortedByDescending { it.category.name }
            }

        setState { reducer.reduce(this, TemplatesIntent.SetFilteredItems(filteredItems)) }
    }

    private fun applyTemplateFilters() {
        val currentState = state.value
        var filteredTemplates = currentState.weeklyTemplates

        // 検索フィルタ
        if (currentState.templateSearchQuery.isNotBlank()) {
            filteredTemplates =
                filteredTemplates.filter { template ->
                    template.title.contains(currentState.templateSearchQuery, ignoreCase = true)
                }
        }

        // 曜日フィルタ
        if (currentState.selectedDayOfWeek != null) {
            filteredTemplates =
                filteredTemplates.filter { template ->
                    template.daysOfWeek.contains(currentState.selectedDayOfWeek)
                }
        }

        // 並び替え
        filteredTemplates =
            when (currentState.templateSortOrder) {
                TemplateSortOrder.NAME_ASC -> filteredTemplates.sortedBy { it.title }
                TemplateSortOrder.NAME_DESC -> filteredTemplates.sortedByDescending { it.title }
                TemplateSortOrder.ITEM_COUNT_ASC -> filteredTemplates.sortedBy { it.itemIds.size }
                TemplateSortOrder.ITEM_COUNT_DESC -> filteredTemplates.sortedByDescending { it.itemIds.size }
            }

        setState { reducer.reduce(this, TemplatesIntent.SetFilteredTemplates(filteredTemplates)) }
    }

    private suspend fun getAllItems() {
        val items = withContext(Dispatchers.IO) { itemRepository.getAllItems() }
        setState { reducer.reduce(this, TemplatesIntent.SetAllItems(items)) }
        applyFilters()
    }

    private suspend fun getAllWeeklyTemplates() {
        val templates = withContext(Dispatchers.IO) { weeklyTemplateRepository.getAllTemplates() }
        setState { reducer.reduce(this, TemplatesIntent.SetWeeklyTemplates(templates)) }
        applyTemplateFilters()
    }

    private suspend fun addWeeklyTemplate(
        title: String,
        daysOfWeek: Set<com.segnities007.model.DayOfWeek>,
    ) {
        withContext(Dispatchers.IO) {
            weeklyTemplateRepository.insertTemplate(
                WeeklyTemplate(
                    title = title,
                    daysOfWeek = daysOfWeek,
                    itemIds = emptyList(), // 追加時は空で作成し、後から編集で詰める
                ),
            )
        }
        getAllWeeklyTemplates()
    }

    private suspend fun editWeeklyTemplate(template: WeeklyTemplate) {
        withContext(Dispatchers.IO) { weeklyTemplateRepository.updateTemplate(template) }
        getAllWeeklyTemplates()
        sendEffect { TemplatesEffect.NavigateToWeeklyTemplateList }
    }

    private fun showDeleteConfirmation(template: WeeklyTemplate) {
        setState { reducer.reduce(this, TemplatesIntent.DeleteWeeklyTemplate(template)) }
    }

    private suspend fun confirmDeleteTemplate() {
        val templateToDelete = state.value.templateToDelete
        if (templateToDelete != null) {
            withContext(Dispatchers.IO) { weeklyTemplateRepository.deleteTemplate(templateToDelete) }
            setState { reducer.reduce(this, TemplatesIntent.ConfirmDeleteTemplate) }
            getAllWeeklyTemplates()
            sendEffect { TemplatesEffect.ShowToast("「${templateToDelete.title}」を削除しました") }
        }
    }

    private fun cancelDeleteTemplate() {
        setState { reducer.reduce(this, TemplatesIntent.CancelDeleteTemplate) }
    }

    private fun selectTemplate(template: WeeklyTemplate) {
        setState { reducer.reduce(this, TemplatesIntent.SelectTemplate(template)) }
        sendEffect { TemplatesEffect.NavigateToWeeklyTemplateSelector }
    }

    private fun importIcs(uri: android.net.Uri) =
        viewModelScope.launch {
            setState { copy(isImportingIcs = true) }
            try {
                val templates = withContext(Dispatchers.IO) { icsTemplateRepository.generateTemplatesFromIcs(uri) }
                withContext(Dispatchers.IO) { icsTemplateRepository.saveGeneratedTemplates(templates) }
                getAllWeeklyTemplates()
                setState { copy(isImportingIcs = false) }
                sendEffect { TemplatesEffect.ShowIcsImportResult(templates.size) }
            } catch (e: Exception) {
                setState { copy(isImportingIcs = false) }
                sendEffect { TemplatesEffect.ShowToast("ICSインポート失敗: ${e.message}") }
            }
        }
}
