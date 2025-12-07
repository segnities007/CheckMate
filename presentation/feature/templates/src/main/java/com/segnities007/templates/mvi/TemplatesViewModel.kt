package com.segnities007.templates.mvi

import androidx.lifecycle.viewModelScope
import kotlinx.datetime.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.templates.utils.TemplateFilter
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.ics.GenerateTemplatesFromIcsUseCase
import com.segnities007.usecase.ics.SaveGeneratedTemplatesUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.template.AddTemplateUseCase
import com.segnities007.usecase.template.DeleteTemplateUseCase
import com.segnities007.usecase.template.GetAllTemplatesUseCase
import com.segnities007.usecase.template.UpdateTemplateUseCase
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TemplatesViewModel(
    private val getAllTemplatesUseCase: GetAllTemplatesUseCase,
    private val addTemplateUseCase: AddTemplateUseCase,
    private val updateTemplateUseCase: UpdateTemplateUseCase,
    private val deleteTemplateUseCase: DeleteTemplateUseCase,
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val generateTemplatesFromIcsUseCase: GenerateTemplatesFromIcsUseCase,
    private val saveGeneratedTemplatesUseCase: SaveGeneratedTemplatesUseCase,
) : BaseViewModel<TemplatesIntent, TemplatesState, TemplatesEffect>(
    initialState = TemplatesState(),
) {


    init {
        sendIntent(TemplatesIntent.GetAllWeeklyTemplates)

        // Start collecting items Flow immediately
        viewModelScope.launch {
            getAllItemsUseCase().collect { items ->
                setState {
                    val newState = copy(allItems = items)
                    val filtered = TemplateFilter.applyItemFilters(
                        allItems = items,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        sortOrder = sortOrder,
                    )
                    newState.copy(filteredItems = filtered)
                }
            }
        }
    }

    override suspend fun handleIntent(intent: TemplatesIntent) {
        when (intent) {
            is TemplatesIntent.AddWeeklyTemplate ->
                addWeeklyTemplate(
                    title = intent.title,
                    daysOfWeek = intent.daysOfWeek,
                )

            is TemplatesIntent.EditWeeklyTemplate -> editWeeklyTemplate(intent.weeklyTemplate)
            is TemplatesIntent.DeleteWeeklyTemplate -> setState { copy(templateToDelete = intent.weeklyTemplate) }
            is TemplatesIntent.SelectTemplate -> selectTemplate(intent.weeklyTemplate)

            TemplatesIntent.GetAllWeeklyTemplates -> getAllWeeklyTemplates()
            TemplatesIntent.GetAllItems -> {} // No-op, Flow is already collecting
            is TemplatesIntent.SetAllItems -> setState { copy(allItems = intent.allItems) }
            is TemplatesIntent.SetWeeklyTemplates -> setState { copy(weeklyTemplates = intent.weeklyTemplates) }
            is TemplatesIntent.SetFilteredItems -> setState { copy(filteredItems = intent.filteredItems) }
            is TemplatesIntent.SetFilteredTemplates -> setState { copy(filteredTemplates = intent.filteredTemplates) }

            TemplatesIntent.ShowBottomSheet -> setState { copy(isShowingBottomSheet = true) }
            TemplatesIntent.HideBottomSheet -> setState { copy(isShowingBottomSheet = false) }


            is TemplatesIntent.UpdateSearchQuery -> updateSearchQuery(intent)
            is TemplatesIntent.UpdateSelectedCategory -> updateSelectedCategory(intent)
            is TemplatesIntent.UpdateSortOrder -> updateSortOrder(intent)
            is TemplatesIntent.UpdateTemplateSearchQuery -> updateTemplateSearchQuery(intent)
            is TemplatesIntent.UpdateTemplateSortOrder -> updateTemplateSortOrder(intent)
            is TemplatesIntent.UpdateSelectedDayOfWeek -> updateSelectedDayOfWeek(intent)

            // 削除確認ダイアログ
            TemplatesIntent.ConfirmDeleteTemplate -> confirmDeleteTemplate()
            TemplatesIntent.CancelDeleteTemplate -> setState { copy(templateToDelete = null) }

            is TemplatesIntent.ImportIcsTemplates -> importIcs(intent.uri)
            TemplatesIntent.ShowIcsFilePicker -> sendEffect { TemplatesEffect.LaunchIcsPicker }
            TemplatesIntent.ImportIcsStarted -> setState { copy(isImportingIcs = true) }
            TemplatesIntent.ImportIcsFinished -> setState { copy(isImportingIcs = false) }
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

    private fun applyFilters() {
        val currentState = currentState
        val filteredItems = TemplateFilter.applyItemFilters(
            allItems = currentState.allItems,
            searchQuery = currentState.searchQuery,
            selectedCategory = currentState.selectedCategory,
            sortOrder = currentState.sortOrder,
        )
        setState { copy(filteredItems = filteredItems) }
    }

    private fun applyTemplateFilters() {
        val currentState = currentState
        val filteredTemplates = TemplateFilter.applyTemplateFilters(
            allTemplates = currentState.weeklyTemplates,
            searchQuery = currentState.templateSearchQuery,
            selectedDayOfWeek = currentState.selectedDayOfWeek,
            sortOrder = currentState.templateSortOrder,
        )
        setState { copy(filteredTemplates = filteredTemplates) }
    }

    private fun getAllWeeklyTemplates() {
        execute(
            action = { getAllTemplatesUseCase().getOrThrow() },
            reducer = { templates ->
                val newState = copy(weeklyTemplates = templates)
                val filtered = TemplateFilter.applyTemplateFilters(
                    allTemplates = templates,
                    searchQuery = templateSearchQuery,
                    selectedDayOfWeek = selectedDayOfWeek,
                    sortOrder = templateSortOrder,
                )
                newState.copy(filteredTemplates = filtered)
            }
        )
    }

    private fun addWeeklyTemplate(
        title: String,
        daysOfWeek: Set<DayOfWeek>,
    ) {
        val template = WeeklyTemplate(
            title = title,
            daysOfWeek = daysOfWeek,
            itemIds = emptyList(),
        )
        viewModelScope.launch {
            addTemplateUseCase(template).fold(
                onSuccess = {
                    sendIntent(TemplatesIntent.GetAllWeeklyTemplates)
                    sendEffect { TemplatesEffect.ShowToast("「${template.title}」を追加しました") }
                },
                onFailure = { e ->
                    sendEffect { TemplatesEffect.ShowToast("テンプレートの追加に失敗しました: ${e.message}") }
                }
            )
        }
    }

    private fun editWeeklyTemplate(template: WeeklyTemplate) {
        viewModelScope.launch {
            updateTemplateUseCase(template).fold(
                onSuccess = {
                    sendIntent(TemplatesIntent.GetAllWeeklyTemplates)
                    sendIntent(TemplatesIntent.GetAllWeeklyTemplates)
                    sendEffect { TemplatesEffect.ShowToast("「${template.title}」を更新しました") }
                },
                onFailure = { e ->
                    sendEffect { TemplatesEffect.ShowToast("テンプレートの更新に失敗しました: ${e.message}") }
                }
            )
        }
    }

    private fun confirmDeleteTemplate() {
        val templateToDelete = currentState.templateToDelete
        if (templateToDelete != null) {
            viewModelScope.launch {
                deleteTemplateUseCase(templateToDelete).fold(
                    onSuccess = {
                        setState { copy(templateToDelete = null) }
                        sendIntent(TemplatesIntent.GetAllWeeklyTemplates)
                        sendEffect { TemplatesEffect.ShowToast("「${templateToDelete.title}」を削除しました") }
                    },
                    onFailure = { e ->
                        sendEffect { TemplatesEffect.ShowToast("削除失敗: ${e.message}") }
                    }
                )
            }
        }
    }

    private fun selectTemplate(template: WeeklyTemplate) {
        setState { copy(selectedTemplate = template) }
    }

    private fun importIcs(uri: android.net.Uri) {
        setState { copy(isImportingIcs = true) }

        viewModelScope.launch {
            val templatesResult = generateTemplatesFromIcsUseCase(uri.toString())
            val templates = templatesResult.getOrElse { e ->
                setState { copy(isImportingIcs = false) }
                sendEffect { TemplatesEffect.ShowToast("ICSインポート失敗: ${e.message}") }
                return@launch
            }

            saveGeneratedTemplatesUseCase(templates).getOrElse { e ->
                setState { copy(isImportingIcs = false) }
                sendEffect { TemplatesEffect.ShowToast("テンプレート保存失敗: ${e.message}") }
                return@launch
            }

            sendIntent(TemplatesIntent.GetAllWeeklyTemplates)
            setState { copy(isImportingIcs = false) }
            sendEffect { TemplatesEffect.ShowIcsImportResult(templates.size) }
        }
    }
}
