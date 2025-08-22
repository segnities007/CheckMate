package com.segnities007.setting

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.UserStatus
import com.segnities007.navigation.HubRoute
import com.segnities007.setting.component.AccountButtons
import com.segnities007.setting.component.DataButtons
import com.segnities007.setting.mvi.SettingEffect
import com.segnities007.setting.mvi.SettingIntent
import com.segnities007.setting.mvi.SettingViewModel
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.card.UserStatusCard
import org.koin.compose.koinInject

@Composable
fun SettingScreen(
    innerPadding: PaddingValues,
    userStatus: UserStatus,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val localContext = LocalContext.current
    val settingViewModel: SettingViewModel = koinInject()
    val scrollState = rememberScrollState()

    val targetAlpha by remember {
        derivedStateOf {
            when {
                scrollState.value > 50 -> 0f // 50px以上スクロールしたら非表示
                else -> (1f - scrollState.value / 50f).coerceIn(0f, 1f) // 0-50pxの範囲でフェード
            }
        }
    }
    
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 200),
        label = "navigationBarAlpha"
    )

    LaunchedEffect(Unit) {
        setNavigationBar {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = HubRoute.Setting,
                onNavigate = onNavigate,
            )
        }
        setFab {}
        setTopBar {}

        settingViewModel.effect.collect { effect ->
            when (effect) {
                is SettingEffect.ShowToast -> {
                    Toast.makeText(
                        localContext,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        SettingUi(userStatus, settingViewModel::sendIntent)
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }
}

@Composable
private fun SettingUi(
    userStatus: UserStatus,
    sendIntent: (SettingIntent) -> Unit,
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { sendIntent(SettingIntent.ImportData(it)) }
    }


    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserStatusCard(userStatus)
        
        DataButtons(
            onExportData = {
                sendIntent(SettingIntent.ExportData)
            },
            onImportData = {
                launcher.launch(arrayOf("application/json"))
            },
            onDeleteAllData = {
                // TODO: 全データ削除の実装
            }
        )
        
        AccountButtons(
            onEditProfile = {
                // TODO: プロフィール編集の実装
            },
            onChangePassword = {
                // TODO: パスワード変更の実装
            },
            onLogout = {
                // TODO: ログアウトの実装
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SettingUi(
        userStatus = UserStatus(),
        sendIntent = {},
    )
}
