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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.segnities007.ui.divider.HorizontalDividerWithLabel
import org.koin.compose.koinInject

@Composable
fun SettingScreen(
    innerPadding: PaddingValues,
    setFab: (@Composable () -> Unit) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
    onNavigate: (HubRoute) -> Unit,
) {
    val localContext = LocalContext.current
    val settingViewModel: SettingViewModel = koinInject()
    val state by settingViewModel.state.collectAsState()
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
                is SettingEffect.ShowIcsImportResult -> {
                    Toast.makeText(
                        localContext,
                        "${effect.successCount}個のテンプレートを作成しました",
                        Toast.LENGTH_LONG
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
        SettingUi(settingViewModel::sendIntent)
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
    }

    // 全データ削除確認ダイアログ
    if (state.showDeleteAllDataDialog) {
        AlertDialog(
            onDismissRequest = { settingViewModel.sendIntent(SettingIntent.CancelDeleteAllData) },
            title = { Text("全データの削除") },
            text = { Text("すべてのデータを本当に削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = { settingViewModel.sendIntent(SettingIntent.ConfirmDeleteAllData) }
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { settingViewModel.sendIntent(SettingIntent.CancelDeleteAllData) }
                ) {
                    Text("キャンセル")
                }
            }
        )
    }

    // ICSインポート中ダイアログ
    if (state.isImportingIcs) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("テンプレート作成中") },
            text = { Text("カレンダーからテンプレートを生成しています...") },
            confirmButton = { }
        )
    }
}

@Composable
private fun SettingUi(
    sendIntent: (SettingIntent) -> Unit,
) {

    val jsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { sendIntent(SettingIntent.ImportData(it)) }
    }

    val icsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { sendIntent(SettingIntent.ImportIcsFile(it)) }
    }

    val settingViewModel: SettingViewModel = koinInject()
    val state by settingViewModel.state.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserStatusCard(state.userStatus)
        
        // データ管理セクション
        HorizontalDividerWithLabel("データ管理")
        DataButtons(
            onExportData = {
                sendIntent(SettingIntent.ExportData)
            },
            onImportData = {
                jsonLauncher.launch(arrayOf("application/json"))
            },
            onImportIcsFile = {
                icsLauncher.launch(arrayOf("text/calendar"))
            },
            onDeleteAllData = {
                sendIntent(SettingIntent.DeleteAllData)
            }
        )
        
        // アカウント設定セクション
        HorizontalDividerWithLabel("アカウント設定")
        AccountButtons(
            onGoogleLink = {
                sendIntent(SettingIntent.LinkWithGoogle)
            },
            onGoogleUnlink = {
                sendIntent(SettingIntent.ChangeGoogleAccount)
            },
            isGoogleLinked = state.userStatus.id.isNotEmpty()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SettingUi(
        sendIntent = {},
    )
}
