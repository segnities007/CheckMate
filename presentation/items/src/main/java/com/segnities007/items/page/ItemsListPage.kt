package com.segnities007.items.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.segnities007.items.component.CreateBottomSheet
import com.segnities007.items.component.SearchFilterBar
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.ItemsState
import com.segnities007.model.item.Item
import com.segnities007.navigation.HubRoute
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import kotlin.time.ExperimentalTime
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.segnities007.items.component.ItemsList
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxSize

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ItemsListPage(
    innerPadding: PaddingValues,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
    sendIntent: (ItemsIntent) -> Unit,
    onNavigateToCameraCapture: () -> Unit,
    onNavigateToItemsList: () -> Unit,
    state: ItemsState,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val granted = {
        sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
        sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(null))
        sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(""))
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                granted()
            }
        }

    val targetAlpha by remember {
        derivedStateOf {
            when {
                scrollState.value > 50 -> 0f // 50px以上スクロールしたら非表示
                else -> (1f - scrollState.value / 50f).coerceIn(0f, 1f) // 0-50pxの範囲でフェード
            }
        }
    }
    
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 200),
        label = "navigationBarAlpha"
    )

    LaunchedEffect(Unit) {
        setNavigationBar {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = HubRoute.Items,
                onNavigate = onNavigate,
            )
        }
        setTopBar {}
        setFab {
            if (alpha > 0) {
                FloatingActionButton(
                    containerColor = FloatingActionButtonDefaults.containerColor.copy(alpha = alpha),
                    contentColor = contentColorFor(FloatingActionButtonDefaults.containerColor).copy(alpha = alpha),
                    onClick = {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA,
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                granted()
                            } else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 検索・フィルタ・ソートバー
            SearchFilterBar(
                searchQuery = state.searchQuery,
                selectedCategory = state.selectedCategory,
                sortOrder = state.sortOrder,
                onSearchQueryChange = { query ->
                    sendIntent(ItemsIntent.UpdateSearchQuery(query))
                },
                onCategoryChange = { category ->
                    sendIntent(ItemsIntent.UpdateSelectedCategory(category))
                },
                onSortOrderChange = { sortOrder ->
                    sendIntent(ItemsIntent.UpdateSortOrder(sortOrder))
                },
            )

            HorizontalDividerWithLabel("アイテム一覧")
            
            ItemsList(
                state = state.copy(items = state.filteredItems),
                onDeleteItem = { itemToDelete ->
                    sendIntent(ItemsIntent.DeleteItems(itemToDelete.id))
                },
            )
        }
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }

    if (state.isShowBottomSheet) {
        val bottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        CreateBottomSheet(
            sheetState = bottomSheetState,
            onDismiss = {
                sendIntent(ItemsIntent.UpdateIsShowBottomSheet(false))
            },
            onCreateItem = { name, description, category, _ ->
                val newItem =
                    Item(
                        name = name,
                        description = description,
                        category = category,
                        imagePath = state.capturedTempPathForViewModel,
                    )
                sendIntent(ItemsIntent.InsertItems(newItem))
                sendIntent(ItemsIntent.UpdateIsShowBottomSheet(false))
                sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(null))
                sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(""))
            },
            capturedImageUriFromParent = state.capturedImageUriForBottomSheet,
            onRequestLaunchCamera = {
                sendIntent(ItemsIntent.UpdateIsShowBottomSheet(false))
                sendIntent(ItemsIntent.NavigateToCameraCapture)
            },
        )
    }
}
