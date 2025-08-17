package com.segnities007.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.segnities007.navigation.HubRoute
import com.segnities007.templates.component.WeeklyTemplateList
import com.segnities007.templates.mvi.TemplatesState
import com.segnities007.templates.mvi.TemplatesViewModel
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.button.SettingFab
import org.koin.compose.koinInject

@Composable
fun TemplatesScreen(
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val templatesViewModel: TemplatesViewModel = koinInject()
    val state by templatesViewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    val alpha by remember {
        derivedStateOf {
            (1f - scrollState.value / 100f).coerceIn(0f, 1f)
        }
    }


    LaunchedEffect(Unit) {
        setNavigationBar{
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = HubRoute.Templates,
                onNavigate = onNavigate
            )
        }
    }

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ){
        TemplatesUi(state)
    }

}

@Composable
private fun TemplatesUi(
    state: TemplatesState,
) {
    WeeklyTemplateList(
        templates = state.weeklyTemplates
    )
}
