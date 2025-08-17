package com.segnities007.items

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.segnities007.items.component.CameraCapture
import com.segnities007.items.component.CreateBottomSheet
import com.segnities007.items.component.ItemsList
import com.segnities007.items.mvi.ItemEffect
import com.segnities007.items.mvi.ItemIntent
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.model.Item
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ItemsScreen(
    updateIsValidScroll: (Boolean) -> Unit,
    showToast: (String) -> Unit,
    showNavigationBar: () -> Unit,
    hideNavigationBar: () -> Unit,
) {
    val itemsViewModel: ItemsViewModel = koinInject()
    val state by itemsViewModel.state.collectAsStateWithLifecycle()

    var showCamera by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var capturedImageUriForBottomSheet by remember { mutableStateOf<Uri?>(null) }
    var capturedTempPathForViewModel by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        itemsViewModel.effect.collect { effect ->
            when (effect) {
                is ItemEffect.ShowToast -> showToast(effect.message)
            }
        }
    }

    LaunchedEffect(showCamera) {
        if (showCamera) {
            updateIsValidScroll(false)
            hideNavigationBar()
        } else {
            updateIsValidScroll(true)
            showNavigationBar()
        }
    }

    if (showCamera) {
        CameraCapture(
            onImageCaptured = { uri, path ->
                showCamera = false
                capturedImageUriForBottomSheet = uri
                capturedTempPathForViewModel = path
            },
            onCancel = {
                showCamera = false
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
                showCamera = false
            },
            onDeleteItem = { itemToDelete ->
                itemsViewModel.sendIntent(ItemIntent.DeleteItem(itemToDelete.id))
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
                    itemsViewModel.sendIntent(ItemIntent.InsertItem(newItem))
                    showBottomSheet = false
                    capturedImageUriForBottomSheet = null
                    capturedTempPathForViewModel = null
                },
                capturedImageUriFromParent = capturedImageUriForBottomSheet,
                onRequestLaunchCamera = {
                    showBottomSheet = true
                    showCamera = true
                },
            )
        }
    }
}
