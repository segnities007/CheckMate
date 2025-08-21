package com.segnities007.items.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.segnities007.items.component.CameraCapture
import com.segnities007.items.mvi.ItemsIntent

@Composable
fun CameraCapturePage(
    onImageCaptured: (android.net.Uri, String) -> Unit,
    onCancel: () -> Unit,
    sendIntent: (ItemsIntent) -> Unit,
    onNavigateToItemsList: () -> Unit,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
) {
    LaunchedEffect(Unit) {
        // CameraCapture画面ではNavigationBar、TopBar、Fabを非表示にする
        setNavigationBar {}
        setTopBar {}
        setFab {}
    }

    CameraCapture(
        onImageCaptured = { uri, path ->
            sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
            sendIntent(ItemsIntent.UpdateCapturedImageUriForBottomSheet(uri))
            sendIntent(ItemsIntent.UpdateCapturedTempPathForViewModel(path))
            onNavigateToItemsList()
        },
        onCancel = {
            onNavigateToItemsList()
        },
    )
}
