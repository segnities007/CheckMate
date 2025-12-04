package com.segnities007.items.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.segnities007.items.component.BarcodeScanner
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.model.item.BarcodeInfo

import com.segnities007.ui.scaffold.CheckMateScaffold

@Composable
fun BarcodeScannerPage(
    onBarcodeDetected: (BarcodeInfo) -> Unit,
    onCancel: () -> Unit,
    sendIntent: (ItemsIntent) -> Unit,
) {
    CheckMateScaffold {
        BarcodeScanner(
            onBarcodeDetected = { barcodeInfo ->
                onBarcodeDetected(barcodeInfo)
            },
            onCancel = {
                onCancel()
            },
        )
    }
}
