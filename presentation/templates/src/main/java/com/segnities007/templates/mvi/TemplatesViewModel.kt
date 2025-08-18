package com.segnities007.templates.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.model.WeeklyTemplate
import com.segnities007.repository.WeeklyTemplateRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.launch

class TemplatesViewModel(
    private val weeklyTemplateRepository: WeeklyTemplateRepository,
) : BaseViewModel<TemplatesIntent, TemplatesState, TemplatesEffect>(
        TemplatesState(),
    ) {
    override suspend fun handleIntent(intent: TemplatesIntent) {
        when (intent) {
            is TemplatesIntent.AddWeeklyTemplate -> addWeeklyTemplate(intent)
            is TemplatesIntent.DeleteWeeklyTemplate -> deleteWeeklyTemplate(intent)
            is TemplatesIntent.EditWeeklyTemplate -> editWeeklyTemplate(intent)
            TemplatesIntent.GetAllWeeklyTemplates -> getAllWeeklyTemplates()
            TemplatesIntent.HideBottomSheet -> hideBottomSheet()
            TemplatesIntent.ShowBottomSheet -> showBottomSheet()
        }
    }

    init {
        getAllWeeklyTemplates()
    }

    private fun showBottomSheet() {
        setState { copy(isShowingBottomSheet = true) }
    }

    private fun hideBottomSheet() {
        setState { copy(isShowingBottomSheet = false) }
    }

    private fun getAllWeeklyTemplates() {
        viewModelScope.launch {
            val templates = weeklyTemplateRepository.getAllTemplates()
            setState { copy(weeklyTemplates = templates) }
        }
    }

    private fun addWeeklyTemplate(intent: TemplatesIntent.AddWeeklyTemplate) {
        viewModelScope.launch {
            weeklyTemplateRepository.insertTemplate(
                WeeklyTemplate(
                    title = intent.title,
                    daysOfWeek = intent.daysOfWeek,
                ),
            )
            getAllWeeklyTemplates()
        }
    }

    private fun editWeeklyTemplate(intent: TemplatesIntent.EditWeeklyTemplate) {
        viewModelScope.launch {
            weeklyTemplateRepository.updateTemplate(intent.weeklyTemplate)
            getAllWeeklyTemplates()
        }
    }

    private fun deleteWeeklyTemplate(intent: TemplatesIntent.DeleteWeeklyTemplate) {
        viewModelScope.launch {
            weeklyTemplateRepository.deleteTemplate(intent.weeklyTemplate)
            getAllWeeklyTemplates()
        }
    }
}
