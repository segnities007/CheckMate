package com.segnities007.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavKeys : NavKey {
    @Serializable
    data object SplashKey : NavKeys

    @Serializable
    sealed interface Auth : NavKeys {
        @Serializable
        data object LoginKey : Auth
    }

    @Serializable
    sealed interface Hub : NavKeys {
        @Serializable
        data object HomeKey : Hub

        @Serializable
        data object DashboardKey : Hub

        @Serializable
        data object SettingKey : Hub

        @Serializable
        sealed interface Items : Hub {
            @Serializable
            data object ListKey : Items

            @Serializable
            data object CameraKey : Items

            @Serializable
            data object BarcodeKey : Items
        }

        @Serializable
        sealed interface Template : Hub {
            @Serializable
            data object ListKey : Template

            @Serializable
            data object SelectorKey : Template
        }
    }
}