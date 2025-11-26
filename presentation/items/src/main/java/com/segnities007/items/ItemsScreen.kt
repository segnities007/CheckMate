package com.segnities007.items

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.segnities007.items.mvi.ItemsEffect
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.items.page.BarcodeScannerPage
import com.segnities007.items.page.CameraCapturePage
import com.segnities007.items.page.ItemsListPage
import com.segnities007.navigation.ItemsRoute
import com.segnities007.navigation.NavKey
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    onNavigate: (NavKey) -> Unit,
) {
    val itemsViewModel: ItemsViewModel = koinInject()
    val state by itemsViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        itemsViewModel.effect.collect { effect ->
            when (effect) {
                is ItemsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                ItemsEffect.NavigateToItemsList -> {
                    // Handled by state
                }
                ItemsEffect.NavigateToCameraCapture -> {
                    // Handled by state
                }
                ItemsEffect.NavigateToBarcodeScanner -> {
                    // Handled by state
                }
                ItemsEffect.ReopenBottomSheetWithProductInfo -> {
                    itemsViewModel.sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
                    itemsViewModel.sendIntent(ItemsIntent.SetShouldClearForm(false))
                }
            }
        }
    }

    val entryProvider = remember {
        entryProvider {
            entry(ItemsRoute.ItemsList) {
                ItemsListPage(
                    onNavigate = onNavigate,
                    sendIntent = itemsViewModel::sendIntent,
                    onNavigateToBarcodeScanner = {
                        itemsViewModel.sendIntent(ItemsIntent.NavigateToBarcodeScanner)
                    },
                    state = state,
                )
            }
            entry(ItemsRoute.CameraCapture) {
                CameraCapturePage(
                    onImageCaptured = { uri, path ->
                        itemsViewModel.sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
                        itemsViewModel.sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(uri))
                        itemsViewModel.sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(path))
                    },
                    onCancel = {
                        itemsViewModel.sendIntent(ItemsIntent.NavigateToItemsList)
                    },
                    sendIntent = itemsViewModel::sendIntent,
                    onNavigateToItemsList = {
                        itemsViewModel.sendIntent(ItemsIntent.NavigateToItemsList)
                    },
                )
            }
            entry(ItemsRoute.BarcodeScanner) {
                BarcodeScannerPage(
                    onBarcodeDetected = { barcodeInfo ->
                        itemsViewModel.sendIntent(ItemsIntent.BarcodeDetected(barcodeInfo))
                        itemsViewModel.sendIntent(ItemsIntent.NavigateToItemsList)
                    },
                    onCancel = {
                        itemsViewModel.sendIntent(ItemsIntent.NavigateToItemsList)
                    },
                    sendIntent = itemsViewModel::sendIntent,
                )
            }
        }
    }

    NavDisplay(
        backStack = listOf(state.currentRoute),
        entryProvider = entryProvider,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}
