package com.segnities007.templates.mvi

import com.segnities007.ui.mvi.MviEffect

sealed interface TemplatesEffect : MviEffect {
    data class ShowToast(
        val message: String,
    ) : TemplatesEffect

    data object NavigateToWeeklyTemplateSelector : TemplatesEffect

    data object NavigateToWeeklyTemplateList : TemplatesEffect

    // ICS インポート用
    data object LaunchIcsPicker : TemplatesEffect

    data class ShowIcsImportResult(
        val successCount: Int,
    ) : TemplatesEffect
}
