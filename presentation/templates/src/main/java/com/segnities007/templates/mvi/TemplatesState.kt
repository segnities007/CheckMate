package com.segnities007.templates.mvi

import com.segnities007.model.Item
import com.segnities007.model.WeeklyTemplate
import com.segnities007.ui.mvi.MviState

data class TemplatesState(
    val weeklyTemplates: List<WeeklyTemplate> = emptyList(),
    val isShowingBottomSheet: Boolean = false,
    val selectedTemplate: WeeklyTemplate = WeeklyTemplate(),
    val allItems: List<Item> = emptyList(),
) : MviState
