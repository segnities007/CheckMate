package com.segnities007.templates.mvi

import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.ui.mvi.MviState

data class TemplatesState(
    val weeklyTemplates: List<WeeklyTemplate> = emptyList(),
    val isLoadingTemplates: Boolean = false,
    val isShowingBottomSheet: Boolean = false,
    val selectedTemplate: WeeklyTemplate? = null, // null で「未選択」を表現
    val allItems: List<Item> = emptyList(),
) : MviState
