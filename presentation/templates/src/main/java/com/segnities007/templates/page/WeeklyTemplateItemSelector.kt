package com.segnities007.templates.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.segnities007.model.Item
import com.segnities007.model.WeeklyTemplate
import com.segnities007.templates.mvi.TemplatesIntent
import com.segnities007.ui.bar.ConfirmBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyTemplateSelector(
    template: WeeklyTemplate,
    allItems: List<Item>,
    innerPadding: PaddingValues,
    sendIntent: (TemplatesIntent) -> Unit,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
) {
    val selectedStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            putAll(template.itemStates)
        }
    }

    LaunchedEffect(Unit) {
        setFab {}
        setTopBar {}
        setNavigationBar {
            ConfirmBar(
                onConfirm = {
                    sendIntent(
                        TemplatesIntent.EditWeeklyTemplate(
                            template.copy(itemStates = selectedStates.toMap())
                        )
                    )
                },
                onCancel = {
                    sendIntent(TemplatesIntent.NavigateToWeeklyTemplateList)
                },
            )
        }
    }

    WeeklyTemplateGridContent(
        innerPadding = innerPadding,
        allItems = allItems,
        selectedStates = selectedStates
    )
}

@Composable
fun WeeklyTemplateGridContent(
    innerPadding: PaddingValues,
    selectedStates: SnapshotStateMap<Int, Boolean>,
    allItems: List<Item>,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = innerPadding,
    ) {
        item(span = { GridItemSpan(maxLineSpan) }){
            HorizontalDividerWithLabel("登録・未登録のアイテム")
        }
        items(allItems, key = { it.id }) { item ->
            val isInTemplate = selectedStates.containsKey(item.id)

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable {
                        if (isInTemplate) {
                            selectedStates.remove(item.id)
                        } else {
                            selectedStates[item.id] = false // 初期状態は未チェック
                        }
                    },
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (isInTemplate) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    Box(modifier = Modifier.weight(4f)) {
                        if (item.imagePath != null) {
                            AsyncImage(
                                model = item.imagePath,
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.1f
                                        )
                                    )
                            )
                        }

                        if (isInTemplate) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun WeeklyTemplateGridContentPreview() {
    val dummyItems =
        listOf(
            Item(id = 1, name = "歯ブラシ", description = "旅行用", imagePath = "https://placehold.co/100x100"),
            Item(id = 2, name = "ノート", description = "大学の授業用", imagePath = "https://placehold.co/100x100"),
            Item(id = 3, name = "充電器", description = "スマホ用", imagePath = null),
            Item(id = 4, name = "マスク", description = "予備", imagePath = null),
        )
    val dummyTemplate =
        WeeklyTemplate(
            id = 1,
            title = "旅行テンプレート",
            itemStates = mapOf(1 to false, 3 to true), // 1と3がテンプレートに含まれている
        )

    MaterialTheme {
        WeeklyTemplateGridContent(
            allItems = dummyItems,
            innerPadding = PaddingValues(0.dp),
            selectedStates = remember {
                mutableStateMapOf<Int, Boolean>().apply { putAll(dummyTemplate.itemStates) }
            },
        )
    }
}
