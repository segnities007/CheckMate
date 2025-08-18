package com.segnities007.items

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.segnities007.items.component.CameraCapture
import com.segnities007.items.component.CreateBottomSheet
import com.segnities007.items.component.ItemsList
import com.segnities007.items.mvi.ItemsEffect
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.ItemsState
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.model.Item
import com.segnities007.navigation.HubRoute
import com.segnities007.ui.bar.FloatingNavigationBar
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ItemsScreen(
    innerPadding: PaddingValues,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val itemsViewModel: ItemsViewModel = koinInject()
    val state by itemsViewModel.state.collectAsStateWithLifecycle()
    var showCamera by remember { mutableStateOf(false) }
    val updateShowCamera: (Boolean) -> Unit = { showCamera = it }
    val scrollState = rememberScrollState()

    val alpha by remember {
        derivedStateOf {
            (1f - scrollState.value / 100f).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(Unit) {
        itemsViewModel.effect.collect { effect ->
            when (effect) {
                is ItemsEffect.ShowToast -> {
                    // TODO
                }
            }
        }
    }

    LaunchedEffect(showCamera) {
        if (showCamera) {
            setNavigationBar {}
        } else {
            setNavigationBar {
                FloatingNavigationBar(
                    alpha = alpha,
                    currentHubRoute = HubRoute.Items,
                    onNavigate = onNavigate,
                )
            }
        }
    }

    Column(
        modifier =
            Modifier.then(
                if (showCamera) {
                    Modifier
                } else {
                    Modifier.verticalScroll(scrollState)
                },
            ),
    ) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        ItemsUi(
            showCamera = showCamera,
            state = state,
            sendIntent = itemsViewModel::sendIntent,
            updateShowCamera = updateShowCamera,
        )
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun ItemsUi(
    state: ItemsState,
    showCamera: Boolean,
    sendIntent: (ItemsIntent) -> Unit,
    updateShowCamera: (Boolean) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var capturedImageUriForBottomSheet by remember { mutableStateOf<Uri?>(null) }
    var capturedTempPathForViewModel by remember { mutableStateOf<String?>(null) }

    if (showCamera) {
        CameraCapture(
            onImageCaptured = { uri, path ->
                updateShowCamera(false)
                capturedImageUriForBottomSheet = uri
                capturedTempPathForViewModel = path
            },
            onCancel = {
                updateShowCamera(false)
            },
        )
    }

    if (!showCamera) {
        ItemsList(
            state = state,
            onAddItem = {
                capturedImageUriForBottomSheet = null
                capturedTempPathForViewModel = null
                showBottomSheet = true
            },
            onDeleteItem = { itemToDelete ->
                sendIntent(ItemsIntent.DeleteItems(itemToDelete.id))
            },
        )

        if (showBottomSheet) {
            val bottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            CreateBottomSheet(
                sheetState = bottomSheetState,
                onDismiss = { showBottomSheet = false },
                onCreateItem = { name, description, category, _ ->
                    val newItem =
                        Item(
                            name = name,
                            description = description,
                            category = category,
                            imagePath = capturedTempPathForViewModel,
                        )
                    sendIntent(ItemsIntent.InsertItems(newItem))
                    showBottomSheet = false
                    capturedImageUriForBottomSheet = null
                    capturedTempPathForViewModel = null
                },
                capturedImageUriFromParent = capturedImageUriForBottomSheet,
                onRequestLaunchCamera = {
                    showBottomSheet = true
                    updateShowCamera(true)
                },
            )
        }
    }
}
