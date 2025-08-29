package com.segnities007.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ItemsRoute {
    @Serializable
    data object ItemsList : ItemsRoute

    @Serializable
    data object CameraCapture : ItemsRoute

    @Serializable
    data object BarcodeScanner : ItemsRoute
}
