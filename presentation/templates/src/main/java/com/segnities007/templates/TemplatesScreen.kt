package com.segnities007.templates

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
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
import com.segnities007.templates.page.TemplateList
import com.segnities007.templates.page.TemplateSelector
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    innerPadding: PaddingValues,
    backgroundBrush: Brush,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val templatesViewModel: TemplatesViewModel = koinInject()
    val state by templatesViewModel.state.collectAsState()
    val localContext = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val navController = rememberNavController()

    val icsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: android.net.Uri? ->
            uri?.let { templatesViewModel.sendIntent(TemplatesIntent.ImportIcsTemplates(it)) }
        }

    LaunchedEffect(Unit) {
        templatesViewModel.effect.collect { effect ->
            when (effect) {
                is TemplatesEffect.NavigateToWeeklyTemplateSelector ->
                    navController.navigate(TemplatesRoute.WeeklyTemplateSelector)
                TemplatesEffect.NavigateToWeeklyTemplateList ->
                    navController.navigate(TemplatesRoute.WeeklyTemplateList)
                is TemplatesEffect.ShowToast ->
                    Toast.makeText(localContext, effect.message, Toast.LENGTH_SHORT).show()
                TemplatesEffect.LaunchIcsPicker ->
                    if (state.isShowingBottomSheet) {
                        icsLauncher.launch(arrayOf("text/calendar"))
                    }
                is TemplatesEffect.ShowIcsImportResult ->
                    Toast.makeText(localContext, "${effect.successCount}件のテンプレートを生成", Toast.LENGTH_LONG).show()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = TemplatesRoute.WeeklyTemplateList,
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        composable<TemplatesRoute.WeeklyTemplateList> {
            TemplateList(
                innerPadding = innerPadding,
                backgroundBrush = backgroundBrush,
                setFab = setFab,
                setTopBar = setTopBar,
                setNavigationBar = setNavigationBar,
                onNavigate = onNavigate,
                sendIntent = templatesViewModel::sendIntent,
                templates = state.filteredTemplates,
                templateSearchQuery = state.templateSearchQuery,
                templateSortOrder = state.templateSortOrder,
                selectedDayOfWeek = state.selectedDayOfWeek,
                onTemplateClick = { templatesViewModel.sendIntent(TemplatesIntent.SelectTemplate(it)) },
                onSearchQueryChange = { query -> templatesViewModel.sendIntent(TemplatesIntent.UpdateTemplateSearchQuery(query)) },
                onSortOrderChange = { sortOrder -> templatesViewModel.sendIntent(TemplatesIntent.UpdateTemplateSortOrder(sortOrder)) },
                onDayOfWeekChange = { dayOfWeek -> templatesViewModel.sendIntent(TemplatesIntent.UpdateSelectedDayOfWeek(dayOfWeek)) },
            )
        }
        composable<TemplatesRoute.WeeklyTemplateSelector> {
            TemplateSelector(
                backgroundBrush = backgroundBrush,
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
            onImportFromIcs = { templatesViewModel.sendIntent(TemplatesIntent.ShowIcsFilePicker) },
            isImportingIcs = state.isImportingIcs,
        )
    }

    // ICSインポート中ダイアログ（SettingScreenと同一コンポーネントを再利用）
    if (state.isImportingIcs) {
        AlertDialog(
            onDismissRequest = { /* no dismiss while importing */ },
            title = { Text("作成中") },
            text = { Text("テンプレートを生成しています...") },
            confirmButton = {},
        )
    }

    // 削除確認ダイアログ
    state.templateToDelete?.let { template ->
        AlertDialog(
            onDismissRequest = { templatesViewModel.sendIntent(TemplatesIntent.CancelDeleteTemplate) },
            title = { Text("テンプレートの削除") },
            text = { Text("「${template.title}」を本当に削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = { templatesViewModel.sendIntent(TemplatesIntent.ConfirmDeleteTemplate) },
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { templatesViewModel.sendIntent(TemplatesIntent.CancelDeleteTemplate) },
                ) {
                    Text("キャンセル")
                }
            },
        )
    }
}
