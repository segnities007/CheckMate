package com.segnities007.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.segnities007.model.UserStatus
import com.segnities007.navigation.HubRoute
import com.segnities007.navigation.Route
import com.segnities007.setting.mvi.SettingEffect
import com.segnities007.setting.mvi.SettingViewModel
import org.koin.compose.koinInject

@Composable
fun SettingScreen(
    userStatus: UserStatus,
    currentRoute: HubRoute,
) {
    val settingViewModel: SettingViewModel = koinInject()

    LaunchedEffect(Unit) {
        settingViewModel.effect.collect { effect ->
            when (effect) {
                is SettingEffect.ShowToast -> {
                    //TODO
                }
            }
        }
    }

    SettingUi(userStatus)
}

@Composable
private fun SettingUi(
    userStatus: UserStatus,
) {

}
