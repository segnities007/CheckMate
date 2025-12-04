package com.segnities007.items

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.segnities007.items.mvi.ItemsEffect
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.items.page.ItemsListPage
import com.segnities007.navigation.NavKeys
import com.segnities007.ui.mvi.UiState
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    onNavigate: (NavKeys) -> Unit,
) {
    val itemsViewModel: ItemsViewModel = koinInject()
    val uiState by itemsViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Effect handling
    LaunchedEffect(Unit) {
        itemsViewModel.effect.collect { effect ->
            when (effect) {
                is ItemsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                ItemsEffect.ReopenBottomSheetWithProductInfo -> {
                    itemsViewModel.sendIntent(ItemsIntent.UpdateIsShowBottomSheet(true))
                    itemsViewModel.sendIntent(ItemsIntent.SetShouldClearForm(false))
                }
            }
        }
    }

    val innerUiState by itemsViewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        ItemsListPage(
            onNavigate = onNavigate,
            sendIntent = itemsViewModel::sendIntent,
            onNavigateToBarcodeScanner = {
                onNavigate(NavKeys.Hub.Items.BarcodeKey)
            },
            onNavigateToCameraCapture = {
                onNavigate(NavKeys.Hub.Items.CameraKey)
            },
            state = innerUiState.data,
        )

        if (uiState is UiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (uiState is UiState.Failure) {
            val message = (uiState as UiState.Failure).message
            LaunchedEffect(message) {
                Toast.makeText(context, "エラーが発生しました: $message", Toast.LENGTH_LONG).show()
            }
        }
    }
}

