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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.UserStatus
import com.segnities007.navigation.NavKey
import com.segnities007.setting.component.AccountButtons
import com.segnities007.setting.component.DataButtons
import com.segnities007.setting.component.DeleteAllDataDialog
import com.segnities007.setting.component.ImportingDialog
import com.segnities007.setting.mvi.SettingEffect
import com.segnities007.setting.mvi.SettingIntent
import com.segnities007.setting.mvi.SettingState
import com.segnities007.setting.mvi.SettingViewModel
import com.segnities007.ui.bar.FloatingNavigationBar
import com.segnities007.ui.card.UserStatusCard
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import com.segnities007.ui.util.rememberScrollVisibility
import org.koin.compose.koinInject

import com.segnities007.ui.scaffold.CheckMateScaffold

import com.segnities007.ui.theme.checkMateBackgroundBrush

@Composable
fun SettingScreen(
    onNavigate: (NavKey) -> Unit,
) {
    val localContext = LocalContext.current
    val settingViewModel: SettingViewModel = koinInject()
    val state by settingViewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val isVisible by rememberScrollVisibility(scrollState)

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "navigationBarAlpha",
    )

    LaunchedEffect(Unit) {
        settingViewModel.effect.collect { effect ->
            when (effect) {
                is SettingEffect.ShowToast -> {
                    Toast
                        .makeText(
                            localContext,
                            effect.message,
                            Toast.LENGTH_SHORT,
                        ).show()
                }
                is SettingEffect.ShowIcsImportResult -> {
                    Toast
                        .makeText(
                            localContext,
                            "${effect.successCount}個のテンプレートを作成しました",
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    CheckMateScaffold(
        bottomBar = {
            FloatingNavigationBar(
                alpha = alpha,
                currentHubRoute = NavKey.Setting,
                onNavigate = onNavigate,
            )
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.checkMateBackgroundBrush)
                    .verticalScroll(scrollState),
        ) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            SettingUi(state = state, sendIntent = settingViewModel::sendIntent)
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        }
    }

    // 全データ削除確認ダイアログ
    if (state.showDeleteAllDataDialog) {
        DeleteAllDataDialog(
            onConfirm = { settingViewModel.sendIntent(SettingIntent.ConfirmDeleteAllData) },
            onDismiss = { settingViewModel.sendIntent(SettingIntent.CancelDeleteAllData) },
        )
    }

    // ICSインポート中ダイアログ
    if (state.isImportingIcs) {
        ImportingDialog()
    }
}

@Composable
private fun SettingUi(
    state: SettingState,
    sendIntent: (SettingIntent) -> Unit,
) {
    val jsonLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri: Uri? ->
            uri?.let { sendIntent(SettingIntent.ImportData(it)) }
        }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserStatusCard(state.userStatus)

        // データ管理セクション
        HorizontalDividerWithLabel("データ")
        DataButtons(
            onExportData = {
                sendIntent(SettingIntent.ExportData)
            },
            onImportData = {
                jsonLauncher.launch(arrayOf("application/json"))
            },
            // ICS インポートは Templates 画面へ移動
            onDeleteAllData = {
                sendIntent(SettingIntent.DeleteAllData)
            },
        )

        // アカウント設定セクション
        HorizontalDividerWithLabel("アカウント")
        AccountButtons(
            onGoogleLink = {
                sendIntent(SettingIntent.LinkWithGoogle)
            },
            onGoogleUnlink = {
                sendIntent(SettingIntent.ChangeGoogleAccount)
            },
            isGoogleLinked = state.userStatus.id.isNotEmpty(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SettingUi(
        state = SettingState(
            userStatus = UserStatus(),
        ),
        sendIntent = {},
    )
}
