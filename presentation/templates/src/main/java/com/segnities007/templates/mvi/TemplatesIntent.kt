package com.segnities007.templates.mvi

import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.ui.mvi.MviIntent

sealed interface TemplatesIntent : MviIntent {
    data object GetAllWeeklyTemplates : TemplatesIntent

    data object GetAllItems : TemplatesIntent

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

    data object ShowBottomSheet : TemplatesIntent

    data object HideBottomSheet : TemplatesIntent
}
