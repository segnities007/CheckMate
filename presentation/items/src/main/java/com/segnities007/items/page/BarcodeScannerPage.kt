package com.segnities007.items.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.segnities007.items.component.BarcodeScanner
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.model.item.BarcodeInfo

@Composable
fun BarcodeScannerPage(
    onBarcodeDetected: (BarcodeInfo) -> Unit,
    onCancel: () -> Unit,
    sendIntent: (ItemsIntent) -> Unit,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
) {
    LaunchedEffect(Unit) {
        // BarcodeScanner画面ではNavigationBar、TopBar、Fabを非表示にする
        setNavigationBar {}
        setTopBar {}
        setFab {}
    }

    BarcodeScanner(
        onBarcodeDetected = { barcodeInfo ->
            onBarcodeDetected(barcodeInfo)
        },
        onCancel = {
            onCancel()
        },
    )
}
