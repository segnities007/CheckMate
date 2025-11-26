package com.segnities007.items.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.segnities007.items.component.CameraCapture
import com.segnities007.items.mvi.ItemsIntent

import com.segnities007.ui.scaffold.CheckMateScaffold

@Composable
fun CameraCapturePage(
    onImageCaptured: (android.net.Uri, String) -> Unit,
    onCancel: () -> Unit,
    sendIntent: (ItemsIntent) -> Unit,
    onNavigateToItemsList: () -> Unit,
) {
    CheckMateScaffold {
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
}
