package com.segnities007.templates.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.navigation.NavKey
import com.segnities007.ui.card.TemplateCard
import com.segnities007.templates.component.TemplateSearchFilterSortBar
import com.segnities007.ui.card.empty.TemplatesEmptyStateCard
import com.segnities007.templates.mvi.TemplateSortOrder
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import com.segnities007.ui.util.rememberScrollVisibility

import com.segnities007.ui.scaffold.CheckMateScaffold
import com.segnities007.ui.theme.checkMateBackgroundBrush

@Composable
fun TemplateListPage(
    onNavigate: (NavKey) -> Unit,
    sendIntent: (TemplatesIntent) -> Unit,
    templates: List<WeeklyTemplate>,
    templateSearchQuery: String,
    templateSortOrder: TemplateSortOrder,
    selectedDayOfWeek: DayOfWeek?,
    onTemplateClick: (WeeklyTemplate) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSortOrderChange: (TemplateSortOrder) -> Unit,
    onDayOfWeekChange: (DayOfWeek?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberScrollState()
    val isVisible by rememberScrollVisibility(listState)

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "navigationBarAlpha",
    )

    CheckMateScaffold(
        bottomBar = {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = NavKey.Templates,
                onNavigate = onNavigate,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.graphicsLayer(alpha = alpha),
                onClick = { sendIntent(TemplatesIntent.ShowBottomSheet) },
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                containerColor = FloatingActionButtonDefaults.containerColor.copy(alpha = alpha),
                contentColor = contentColorFor(FloatingActionButtonDefaults.containerColor).copy(alpha = alpha),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = "Add Template",
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(MaterialTheme.checkMateBackgroundBrush)
                    .verticalScroll(listState)
                    .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            TemplateListUi(
                sendIntent = sendIntent,
                templates = templates,
                templateSearchQuery = templateSearchQuery,
                templateSortOrder = templateSortOrder,
                selectedDayOfWeek = selectedDayOfWeek,
                onTemplateClick = onTemplateClick,
                onSearchQueryChange = onSearchQueryChange,
                onSortOrderChange = onSortOrderChange,
                onDayOfWeekChange = onDayOfWeekChange,
            )
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        }
    }
}

@Composable
private fun TemplateListUi(
    sendIntent: (TemplatesIntent) -> Unit,
    templates: List<WeeklyTemplate>,
    templateSearchQuery: String,
    templateSortOrder: TemplateSortOrder,
    selectedDayOfWeek: DayOfWeek?,
    onTemplateClick: (WeeklyTemplate) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSortOrderChange: (TemplateSortOrder) -> Unit,
    onDayOfWeekChange: (DayOfWeek?) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TemplateSearchFilterSortBar(
            searchQuery = templateSearchQuery,
            sortOrder = templateSortOrder,
            selectedDayOfWeek = selectedDayOfWeek,
            onSearchQueryChange = onSearchQueryChange,
            onSortOrderChange = onSortOrderChange,
            onDayOfWeekChange = onDayOfWeekChange,
        )
        HorizontalDividerWithLabel(
            label = "テンプレート一覧",
        )

        if (templates.isEmpty()) {
            TemplatesEmptyStateCard(
                onCreateClick = { sendIntent(TemplatesIntent.ShowBottomSheet) },
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ){
                templates.forEach { template ->
                    TemplateCard(
                        template = template,
                        onClick = { onTemplateClick(template) },
                    ) {
                        IconButton(
                            onClick = { sendIntent(TemplatesIntent.DeleteWeeklyTemplate(template)) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "削除",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeeklyTemplateListPreview() {
    val dummyTemplates =
        listOf(
            WeeklyTemplate(
                id = 1,
                title = "月曜日の忘れ物",
                itemIds = listOf(1, 2, 3),
            ),
            WeeklyTemplate(
                id = 2,
                title = "火曜日の忘れ物",
                itemIds = listOf(1, 2, 3),
            ),
        )

    TemplateListUi(
        sendIntent = {},
        templates = dummyTemplates,
        templateSearchQuery = "",
        templateSortOrder = TemplateSortOrder.NAME_ASC,
        selectedDayOfWeek = null,
        onTemplateClick = {},
        onSearchQueryChange = {},
        onSortOrderChange = {},
        onDayOfWeekChange = {},
    )
}
