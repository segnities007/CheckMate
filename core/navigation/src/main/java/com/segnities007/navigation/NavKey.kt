package com.segnities007.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavKey {
    @Serializable
    data object Auth : NavKey

    @Serializable
    data object Hub : NavKey

    @Serializable
    data object Home : NavKey

    @Serializable
    data object Items : NavKey

    @Serializable
    data object Dashboard : NavKey

    @Serializable
    data object Templates : NavKey

    @Serializable
    data object Setting : NavKey

    @Serializable
    data object ItemsList : NavKey

    @Serializable
    data object CameraCapture : NavKey

    @Serializable
    data object BarcodeScanner : NavKey

    @Serializable
    data object WeeklyTemplateList : NavKey

    @Serializable
    data object WeeklyTemplateSelector : NavKey

    @Serializable
    data object Splash : NavKey

    @Serializable
    data object Login : NavKey
}