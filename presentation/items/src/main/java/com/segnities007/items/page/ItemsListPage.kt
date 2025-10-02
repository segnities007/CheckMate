package com.segnities007.items.page

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.segnities007.items.component.CreateBottomSheet
import com.segnities007.items.component.ItemsList
import com.segnities007.items.component.SearchFilterBar
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.ItemsState
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.navigation.HubRoute
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import com.segnities007.ui.util.rememberScrollVisibility
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun ItemsListPage(
    innerPadding: PaddingValues,
    backgroundBrush: Brush,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
    sendIntent: (ItemsIntent) -> Unit,
    onNavigateToBarcodeScanner: () -> Unit,
    state: ItemsState,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val isVisible by rememberScrollVisibility(scrollState)

    val granted = {
        // TODO カメラパーミッション許可後の処理
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                granted()
            }
        }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "navigationBarAlpha",
    )

    // 上部バーやFABを親から設定
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
            FloatingActionButton(
                modifier = Modifier.graphicsLayer(alpha = alpha),
                containerColor = FloatingActionButtonDefaults.containerColor,
                contentColor = contentColorFor(FloatingActionButtonDefaults.containerColor),
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                onClick = {
                    sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
                    sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(null))
                    sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(""))
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "アイテムを追加",
                )
            }
        }
    }

    // UI部分を切り出し
    ItemListUi(
        innerPadding = innerPadding,
        scrollState = scrollState,
        brush = backgroundBrush,
        state = state,
        sendIntent = sendIntent,
    )

    // BottomSheet
    if (state.isShowBottomSheet) {
        val bottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        CreateBottomSheet(
            sheetState = bottomSheetState,
            onDismiss = {
                sendIntent(ItemsIntent.UpdateIsShowBottomSheet(false))
                sendIntent(ItemsIntent.ClearProductInfo)
            },
            onCreateItem = { name, description, category, _ ->
                val imagePath = state.productInfo?.imageUrl ?: state.capturedTempPathForViewModel
                val newItem =
                    Item(
                        name = name,
                        description = description,
                        category = category,
                        imagePath = imagePath,
                        barcodeInfo = state.scannedBarcodeInfo,
                        productInfo = state.productInfo,
                    )
                sendIntent(ItemsIntent.InsertItems(newItem))
                sendIntent(ItemsIntent.UpdateIsShowBottomSheet(false))
                sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(null))
                sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(""))
                sendIntent(ItemsIntent.ClearProductInfo)
            },
            capturedImageUriFromParent = state.capturedImageUriForBottomSheet,
            onRequestLaunchCamera = {
                sendIntent(ItemsIntent.UpdateIsShowBottomSheet(false))
                sendIntent(ItemsIntent.NavigateToCameraCapture)
            },
            onRequestBarcodeScan = {
                sendIntent(ItemsIntent.UpdateIsShowBottomSheet(false))
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA,
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    onNavigateToBarcodeScanner()
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            productInfo = state.productInfo,
            shouldClearForm = state.shouldClearForm,
        )
    }
}

@Composable
private fun ItemListUi(
    innerPadding: PaddingValues,
    scrollState: androidx.compose.foundation.ScrollState,
    brush: Brush,
    state: ItemsState,
    sendIntent: (ItemsIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
            .verticalScroll(scrollState),
    ) {
        // Top Padding
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
                onCreateClick = {
                    sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
                    sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(null))
                    sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(""))
                },
            )
        }

        // Bottom Padding
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun ItemListUiPreview() {
    ItemListUi(
        innerPadding = PaddingValues(0.dp),
        scrollState = rememberScrollState(),
        brush = verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.primary.copy(0.2f),
            ),
        ),
        state = ItemsState(
            filteredItems = listOf(
                Item(name = "Test Item", description = "Test Description", category = ItemCategory.STUDY_SUPPLIES),
                Item(name = "Test Item", description = "Test Description", category = ItemCategory.STUDY_SUPPLIES),
                Item(name = "Test Item", description = "Test Description", category = ItemCategory.STUDY_SUPPLIES),
                Item(name = "Test Item", description = "Test Description", category = ItemCategory.STUDY_SUPPLIES),
            )
        ),
        sendIntent = {},
    )
}
