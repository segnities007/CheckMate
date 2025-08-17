package com.segnities007.templates.mvi

import com.segnities007.model.WeeklyTemplate
import com.segnities007.ui.mvi.MviIntent

sealed interface TemplatesIntent: MviIntent {
    data object GetAllWeeklyTemplates: TemplatesIntent
    data class AddWeeklyTemplate(val weeklyTemplate: WeeklyTemplate): TemplatesIntent
    data class EditWeeklyTemplate(val weeklyTemplate: WeeklyTemplate): TemplatesIntent
    data class DeleteWeeklyTemplate(val weeklyTemplate: WeeklyTemplate): TemplatesIntent
}