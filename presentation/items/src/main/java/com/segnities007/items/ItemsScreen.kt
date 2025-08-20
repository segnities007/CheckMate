package com.segnities007.items

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.segnities007.items.page.CameraCapture
import com.segnities007.items.component.CreateBottomSheet
import com.segnities007.items.page.ItemsList
import com.segnities007.items.mvi.ItemsEffect
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.ItemsState
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.model.item.Item
import com.segnities007.navigation.HubRoute
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ItemsScreen(
    innerPadding: PaddingValues,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val itemsViewModel: ItemsViewModel = koinInject()
    val state by itemsViewModel.state.collectAsStateWithLifecycle()
    var showCamera by remember { mutableStateOf(false) }
    val updateShowCamera: (Boolean) -> Unit = { showCamera = it }
    val scrollState = rememberScrollState()
    val granted = {
        itemsViewModel.sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
        itemsViewModel.sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(null))
        itemsViewModel.sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(""))
    }
    val context = LocalContext.current
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                granted()
            }
        }


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
            setTopBar {}
            setFab {}
        }else{
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
                    onClick = {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA,
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                granted()
                            }else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                ){
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                    )
                }
            }
        }
    }

    Column(
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .then(
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
    if (showCamera) {
        CameraCapture(
            onImageCaptured = { uri, path ->
                updateShowCamera(false)
                sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
                sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(uri))
                sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(path))
            },
            onCancel = {
                updateShowCamera(false)
            },
        )
    }

    if (!showCamera) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ){
            HorizontalDividerWithLabel("アイテム一覧")
            ItemsList(
                state = state,
                onDeleteItem = { itemToDelete ->
                    sendIntent(ItemsIntent.DeleteItems(itemToDelete.id))
                },
            )

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
                        updateShowCamera(true)
                    },
                )
            }
        }
    }
}
