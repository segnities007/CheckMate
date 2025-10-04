package com.segnities007.templates.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.model.DayOfWeek
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private fun applyFilters() {
        val currentState = state.value
        val filteredItems = TemplateFilter.applyItemFilters(
            allItems = currentState.allItems,
            searchQuery = currentState.searchQuery,
            selectedCategory = currentState.selectedCategory,
            sortOrder = currentState.sortOrder,
        )
        setState { reducer.reduce(this, TemplatesIntent.SetFilteredItems(filteredItems)) }
    }

    private fun applyTemplateFilters() {
        val currentState = state.value
        val filteredTemplates = TemplateFilter.applyTemplateFilters(
            allTemplates = currentState.weeklyTemplates,
            searchQuery = currentState.templateSearchQuery,
            selectedDayOfWeek = currentState.selectedDayOfWeek,
            sortOrder = currentState.templateSortOrder,
        )
        setState { reducer.reduce(this, TemplatesIntent.SetFilteredTemplates(filteredTemplates)) }
    }

    private suspend fun getAllItems() {
        getAllItemsUseCase().fold(
            onSuccess = { items ->
                setState { reducer.reduce(this, TemplatesIntent.SetAllItems(items)) }
                applyFilters()
            },
            onFailure = { e ->
                sendEffect { TemplatesEffect.ShowToast("アイテムの読み込みに失敗しました") }
            }
        )
    }

    private suspend fun getAllWeeklyTemplates() {
        getAllTemplatesUseCase().fold(
            onSuccess = { templates ->
                setState { reducer.reduce(this, TemplatesIntent.SetWeeklyTemplates(templates)) }
                applyTemplateFilters()
            },
            onFailure = { e ->
                sendEffect { TemplatesEffect.ShowToast("テンプレートの読み込みに失敗しました") }
            }
        )
    }

    private suspend fun addWeeklyTemplate(
        title: String,
        daysOfWeek: Set<DayOfWeek>,
    ) {
        val template = WeeklyTemplate(
            title = title,
            daysOfWeek = daysOfWeek,
            itemIds = emptyList(), // 追加時は空で作成し、後から編集で詰める
        )
        addTemplateUseCase(template).fold(
            onSuccess = {
                getAllWeeklyTemplates()
                sendEffect { TemplatesEffect.ShowToast("「${template.title}」を追加しました") }
            },
            onFailure = { e ->
                sendEffect { TemplatesEffect.ShowToast("テンプレートの追加に失敗しました: ${e.message}") }
            }
        )
    }

    private suspend fun editWeeklyTemplate(template: WeeklyTemplate) {
        updateTemplateUseCase(template).fold(
            onSuccess = {
                getAllWeeklyTemplates()
                sendEffect { TemplatesEffect.NavigateToWeeklyTemplateList }
                sendEffect { TemplatesEffect.ShowToast("「${template.title}」を更新しました") }
            },
            onFailure = { e ->
                sendEffect { TemplatesEffect.ShowToast("テンプレートの更新に失敗しました: ${e.message}") }
            }
        )
    }

    private fun showDeleteConfirmation(template: WeeklyTemplate) {
        setState { reducer.reduce(this, TemplatesIntent.DeleteWeeklyTemplate(template)) }
    }

    private suspend fun confirmDeleteTemplate() {
        val templateToDelete = state.value.templateToDelete
        if (templateToDelete != null) {
            deleteTemplateUseCase(templateToDelete).fold(
                onSuccess = {
                    setState { reducer.reduce(this, TemplatesIntent.ConfirmDeleteTemplate) }
                    getAllWeeklyTemplates()
                    sendEffect { TemplatesEffect.ShowToast("「${templateToDelete.title}」を削除しました") }
                },
                onFailure = { e ->
                    sendEffect { TemplatesEffect.ShowToast("削除失敗: ${e.message}") }
                }
            )
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

            val templates = generateTemplatesFromIcsUseCase(uri).getOrElse { e ->
                setState { copy(isImportingIcs = false) }
                sendEffect { TemplatesEffect.ShowToast("ICSインポート失敗: ${e.message}") }
                return@launch
            }

            saveGeneratedTemplatesUseCase(templates).getOrElse { e ->
                setState { copy(isImportingIcs = false) }
                sendEffect { TemplatesEffect.ShowToast("テンプレート保存失敗: ${e.message}") }
                return@launch
            }

            getAllWeeklyTemplates()
            setState { copy(isImportingIcs = false) }
            sendEffect { TemplatesEffect.ShowIcsImportResult(templates.size) }
        }
}
