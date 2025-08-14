package com.segnities007.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.UserStatus
import com.segnities007.setting.component.AccountButtons
import com.segnities007.setting.component.DataButtons
import com.segnities007.setting.mvi.SettingEffect
import com.segnities007.setting.mvi.SettingViewModel
import com.segnities007.ui.button.RectangleButton
import com.segnities007.ui.card.UserStatusCard
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import org.koin.compose.koinInject

@Composable
fun SettingScreen(
    userStatus: UserStatus,
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserStatusCard(userStatus)
        DataButtons()
        AccountButtons()
        DataButtons()
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SettingUi(
        userStatus = UserStatus(
            name = "John Doe",
            email = "john.doe@example.com",
            pictureUrl = "a"
        )
    )
}
