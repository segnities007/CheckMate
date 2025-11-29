package com.segnities007.templates

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.segnities007.model.WeeklyTemplate
import com.segnities007.navigation.NavKey
import com.segnities007.templates.component.CreateWeeklyTemplateBottomSheet
import com.segnities007.templates.mvi.TemplatesEffect
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.templates.mvi.TemplatesState
import com.segnities007.templates.mvi.TemplatesViewModel
import com.segnities007.templates.page.TemplateItemSelectPage
import com.segnities007.templates.page.TemplateListPage
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    onNavigate: (NavKey) -> Unit,
) {
    val templatesViewModel: TemplatesViewModel = koinInject()
    val uiState by templatesViewModel.uiState.collectAsStateWithLifecycle()
    val state = uiState.data
    val localContext = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val icsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: android.net.Uri? ->
            uri?.let { templatesViewModel.sendIntent(TemplatesIntent.ImportIcsTemplates(it)) }
        }

    // Effect handling
    LaunchedEffect(Unit) {
        templatesViewModel.effect.collect { effect ->
            when (effect) {
                is TemplatesEffect.ShowToast ->
                    Toast.makeText(localContext, effect.message, Toast.LENGTH_SHORT).show()
                TemplatesEffect.LaunchIcsPicker ->
                    if (state.isShowingBottomSheet) {
                        icsLauncher.launch(arrayOf("text/calendar"))
                    }
                is TemplatesEffect.ShowIcsImportResult ->
                    Toast.makeText(localContext, "${effect.successCount}件のテンプレートをインポートしました", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Delegate to Content
    TemplatesContent(
        state = state,
        onIntent = templatesViewModel::sendIntent,
        onNavigate = onNavigate,
        sheetState = sheetState,
        templatesViewModel = templatesViewModel,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplatesContent(
    state: TemplatesState,
    onIntent: (TemplatesIntent) -> Unit,
    onNavigate: (NavKey) -> Unit,
    sheetState: SheetState,
    templatesViewModel: TemplatesViewModel,
) {
    val entryProvider = remember {
        entryProvider {
            entry(NavKey.WeeklyTemplateList) {
                val innerUiState by templatesViewModel.uiState.collectAsStateWithLifecycle()
                val innerState = innerUiState.data
                TemplateListPage(
                    onNavigate = onNavigate,
                    sendIntent = onIntent,
                    templates = innerState.filteredTemplates,
                    templateSearchQuery = innerState.templateSearchQuery,
                    templateSortOrder = innerState.templateSortOrder,
                    selectedDayOfWeek = innerState.selectedDayOfWeek,
                    onTemplateClick = { onIntent(TemplatesIntent.SelectTemplate(it)) },
                    onSearchQueryChange = { query -> onIntent(TemplatesIntent.UpdateTemplateSearchQuery(query)) },
                    onSortOrderChange = { sortOrder -> onIntent(TemplatesIntent.UpdateTemplateSortOrder(sortOrder)) },
                    onDayOfWeekChange = { dayOfWeek -> onIntent(TemplatesIntent.UpdateSelectedDayOfWeek(dayOfWeek)) },
                )
            }
            entry(NavKey.WeeklyTemplateSelector) {
                val innerUiState by templatesViewModel.uiState.collectAsStateWithLifecycle()
                val innerState = innerUiState.data
                TemplateItemSelectPage(
                    sendIntent = onIntent,
                    template = innerState.selectedTemplate ?: WeeklyTemplate(),
                    allItems = innerState.filteredItems.ifEmpty { innerState.allItems },
                )
            }
        }
    }

    NavDisplay(
        backStack = listOf(state.currentRoute),
        entryProvider = entryProvider,
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
    )

    if (state.isShowingBottomSheet) {
        CreateWeeklyTemplateBottomSheet(
            onDismiss = { onIntent(TemplatesIntent.HideBottomSheet) },
            onCreateTemplate = { title, dayOfWeek ->
                onIntent(TemplatesIntent.AddWeeklyTemplate(title, dayOfWeek))
            },
            sheetState = sheetState,
            onImportFromIcs = { onIntent(TemplatesIntent.ShowIcsFilePicker) },
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
            onDismissRequest = { onIntent(TemplatesIntent.CancelDeleteTemplate) },
            title = { Text("テンプレートの削除") },
            text = { Text("「${template.title}」を本当に削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = { onIntent(TemplatesIntent.ConfirmDeleteTemplate) },
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onIntent(TemplatesIntent.CancelDeleteTemplate) },
                ) {
                    Text("キャンセル")
                }
            },
        )
    }
}
