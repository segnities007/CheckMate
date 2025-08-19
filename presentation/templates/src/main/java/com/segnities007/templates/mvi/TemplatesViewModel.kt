// TemplatesViewModel.kt
package com.segnities007.templates.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.launch

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
            is TemplatesIntent.SelectTemplate ->
                setState { copy(selectedTemplate = intent.weeklyTemplate) }
                    .also { sendEffect { TemplatesEffect.NavigateToWeeklyTemplateSelector } }

            TemplatesIntent.GetAllWeeklyTemplates -> getAllWeeklyTemplates()
            TemplatesIntent.GetAllItems -> getAllItems()

            TemplatesIntent.ShowBottomSheet -> setState { copy(isShowingBottomSheet = true) }
            TemplatesIntent.HideBottomSheet -> setState { copy(isShowingBottomSheet = false) }

            TemplatesIntent.NavigateToWeeklyTemplateList ->
                sendEffect { TemplatesEffect.NavigateToWeeklyTemplateList }
            TemplatesIntent.NavigateToWeeklyTemplateSelector ->
                sendEffect { TemplatesEffect.NavigateToWeeklyTemplateSelector }
        }
    }

    private fun getAllItems() {
        viewModelScope.launch {
            val items = itemRepository.getAllItems()
            setState { copy(allItems = items) }
        }
    }

    private fun getAllWeeklyTemplates() {
        viewModelScope.launch {
            val templates = weeklyTemplateRepository.getAllTemplates()
            setState { copy(weeklyTemplates = templates) }
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
}
