package com.segnities007.templates

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.model.WeeklyTemplate
import com.segnities007.navigation.HubRoute
import com.segnities007.navigation.TemplatesRoute
import com.segnities007.templates.component.CreateWeeklyTemplateBottomSheet
import com.segnities007.templates.mvi.TemplatesEffect
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.templates.mvi.TemplatesViewModel
import com.segnities007.templates.page.WeeklyTemplateList
import com.segnities007.templates.page.WeeklyTemplateSelector
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    innerPadding: PaddingValues,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val templatesViewModel: TemplatesViewModel = koinInject()
    val state by templatesViewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        templatesViewModel.effect.collect { effect ->
            when (effect) {
                is TemplatesEffect.NavigateToWeeklyTemplateSelector -> {
                    navController.navigate(TemplatesRoute.WeeklyTemplateSelector)
                }
                is TemplatesEffect.ShowToast -> {
                    // TODO
                }

                TemplatesEffect.NavigateToWeeklyTemplateList ->
                    navController.navigate(TemplatesRoute.WeeklyTemplateList)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = TemplatesRoute.WeeklyTemplateList,
    ) {
        composable<TemplatesRoute.WeeklyTemplateList> {
            WeeklyTemplateList(
                innerPadding = innerPadding,
                setFab = setFab,
                setTopBar = setTopBar,
                setNavigationBar = setNavigationBar,
                onNavigate = onNavigate,
                sendIntent = templatesViewModel::sendIntent,
                templates = state.filteredTemplates,
                allItems = state.allItems,
                templateSearchQuery = state.templateSearchQuery,
                templateSortOrder = state.templateSortOrder,
                selectedDayOfWeek = state.selectedDayOfWeek,
                onTemplateClick = { templatesViewModel.sendIntent(TemplatesIntent.SelectTemplate(it)) },
                onSearchQueryChange = { query -> templatesViewModel.sendIntent(TemplatesIntent.UpdateTemplateSearchQuery(query)) },
                onSortOrderChange = { sortOrder -> templatesViewModel.sendIntent(TemplatesIntent.UpdateTemplateSortOrder(sortOrder)) },
                onDayOfWeekChange = { dayOfWeek -> templatesViewModel.sendIntent(TemplatesIntent.UpdateSelectedDayOfWeek(dayOfWeek)) }
            )
        }
        composable<TemplatesRoute.WeeklyTemplateSelector> {
            WeeklyTemplateSelector(
                sendIntent = templatesViewModel::sendIntent,
                innerPadding = innerPadding,
                setNavigationBar = setNavigationBar,
                setTopBar = setTopBar,
                setFab = setFab,
                template = state.selectedTemplate ?: WeeklyTemplate(),
                allItems = state.filteredItems.ifEmpty { state.allItems },
            )
        }
    }

    if (state.isShowingBottomSheet) {
        CreateWeeklyTemplateBottomSheet(
            onDismiss = { templatesViewModel.sendIntent(TemplatesIntent.HideBottomSheet) },
            onCreateTemplate = { title, dayOfWeek ->
                templatesViewModel.sendIntent(TemplatesIntent.AddWeeklyTemplate(title, dayOfWeek))
            },
            sheetState = sheetState,
        )
    }
}
