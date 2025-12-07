package com.segnities007.templates

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.segnities007.model.WeeklyTemplate
import com.segnities007.common.keys.NavKeys
import com.segnities007.templates.mvi.TemplatesViewModel
import com.segnities007.templates.page.TemplateItemSelectPage
import org.koin.compose.koinInject

fun EntryProviderScope<NavKey>.templatesEntry(
    onNavigate: (NavKeys) -> Unit,
    onBack: () -> Unit,
) {
    entry<NavKeys.Hub.Template.ListKey> {
        TemplatesScreen(onNavigate = onNavigate)
    }

    entry<NavKeys.Hub.Template.SelectorKey> {
        val templatesViewModel: TemplatesViewModel = koinInject()
        val uiState by templatesViewModel.uiState.collectAsStateWithLifecycle()
        val state = uiState.data

        TemplateItemSelectPage(
            sendIntent = templatesViewModel::sendIntent,
            template = state.selectedTemplate ?: WeeklyTemplate(),
            allItems = state.filteredItems.ifEmpty { state.allItems },
            onNavigateBack = { onNavigate(NavKeys.Hub.Template.ListKey) },
        )
    }
}
