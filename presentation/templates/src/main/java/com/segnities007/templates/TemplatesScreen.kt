package com.segnities007.templates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.segnities007.navigation.HubRoute
import com.segnities007.templates.component.CreateWeeklyTemplateBottomSheet
import com.segnities007.templates.component.WeeklyTemplateList
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.templates.mvi.TemplatesState
import com.segnities007.templates.mvi.TemplatesViewModel
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.button.SettingFab
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    innerPadding: PaddingValues,
    setBottomBar: (@Composable () -> Unit) -> Unit,
    setFab: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val templatesViewModel: TemplatesViewModel = koinInject()
    val state by templatesViewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scrollState = rememberScrollState()

    val alpha by remember {
        derivedStateOf {
            (1f - scrollState.value / 100f).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(Unit) {
        setBottomBar {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = HubRoute.Templates,
                onNavigate = onNavigate,
            )
        }
        setFab {
            if (alpha > 0f) {
                FloatingActionButton(
                    onClick = { templatesViewModel.sendIntent(TemplatesIntent.ShowBottomSheet) },
                    containerColor = FloatingActionButtonDefaults.containerColor.copy(alpha = alpha),
                    contentColor = contentColorFor(FloatingActionButtonDefaults.containerColor).copy(alpha = alpha),
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Menu",
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        TemplatesUi(state)
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
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

@Composable
private fun TemplatesUi(state: TemplatesState) {
    WeeklyTemplateList(
        templates = state.weeklyTemplates,
    )
}
