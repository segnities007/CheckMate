package com.segnities007.items

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.items.mvi.ItemsEffect
import com.segnities007.items.mvi.ItemsIntent
import com.segnities007.items.mvi.ItemsViewModel
import com.segnities007.items.page.BarcodeScannerPage
import com.segnities007.items.page.CameraCapturePage
import com.segnities007.items.page.ItemsListPage
import com.segnities007.navigation.HubRoute
import com.segnities007.navigation.ItemsRoute
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        itemsViewModel.effect.collect { effect ->
            when (effect) {
                is ItemsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                ItemsEffect.NavigateToItemsList -> {
                    navController.navigate(ItemsRoute.ItemsList) {
                        popUpTo(ItemsRoute.ItemsList) { inclusive = true }
                    }
                }
                ItemsEffect.NavigateToCameraCapture -> {
                    navController.navigate(ItemsRoute.CameraCapture)
                }
                ItemsEffect.NavigateToBarcodeScanner -> {
                    navController.navigate(ItemsRoute.BarcodeScanner)
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = ItemsRoute.ItemsList,
    ) {
        composable<ItemsRoute.ItemsList> {
            ItemsListPage(
                innerPadding = innerPadding,
                setNavigationBar = setNavigationBar,
                setFab = setFab,
                onNavigate = onNavigate,
                sendIntent = itemsViewModel::sendIntent,
                setTopBar = setTopBar,
                onNavigateToBarcodeScanner = {
                    itemsViewModel.sendIntent(ItemsIntent.NavigateToBarcodeScanner)
                },
                state = state,
            )
        }
        composable<ItemsRoute.CameraCapture> {
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
                setFab = setFab,
                setTopBar = setTopBar,
                setNavigationBar = setNavigationBar,
            )
        }
        composable<ItemsRoute.BarcodeScanner> {
            BarcodeScannerPage(
                onBarcodeDetected = { barcodeInfo ->
                    itemsViewModel.sendIntent(ItemsIntent.BarcodeDetected(barcodeInfo))
                    itemsViewModel.sendIntent(ItemsIntent.NavigateToItemsList)
                },
                onCancel = {
                    itemsViewModel.sendIntent(ItemsIntent.NavigateToItemsList)
                },
                sendIntent = itemsViewModel::sendIntent,
                setFab = setFab,
                setTopBar = setTopBar,
                setNavigationBar = setNavigationBar,
            )
        }
    }
}
