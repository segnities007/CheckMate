package com.segnities007.items

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey


import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.items.page.BarcodeScannerPage
import com.segnities007.items.page.CameraCapturePage
import com.segnities007.common.keys.NavKeys
import com.segnities007.common.keys.NavKeys.Hub.Items
import org.koin.compose.koinInject

fun EntryProviderScope<NavKey>.itemsEntry(
    onNavigate: (NavKeys) -> Unit,
    onBack: () -> Unit,
) {
    entry<Items.ListKey> {
        ItemsScreen(onNavigate = onNavigate)
    }

    entry<Items.CameraKey> {
        val itemsViewModel: ItemsViewModel = koinInject()
        CameraCapturePage(
            onImageCaptured = { uri, path ->
                itemsViewModel.sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
                itemsViewModel.sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(uri))
                itemsViewModel.sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(path))
            },
            onCancel = onBack,
            sendIntent = itemsViewModel::sendIntent,
            onNavigateToItemsList = {
                onNavigate(Items.ListKey)
            },
        )
    }

    entry<Items.BarcodeKey> {
        val itemsViewModel: ItemsViewModel = koinInject()
        BarcodeScannerPage(
            onBarcodeDetected = { barcodeInfo ->
                itemsViewModel.sendIntent(ItemsIntent.BarcodeDetected(barcodeInfo))
                onNavigate(Items.ListKey)
            },
            onCancel = onBack,
            sendIntent = itemsViewModel::sendIntent,
        )
    }
}
