package com.segnities007.navigation

import kotlinx.serialization.Serializable

interface NavKey {
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
}