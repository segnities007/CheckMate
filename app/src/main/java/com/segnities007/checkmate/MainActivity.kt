package com.segnities007.checkmate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.segnities007.auth.AuthNavigation
import com.segnities007.checkmate.mvi.MainIntent
import com.segnities007.checkmate.mvi.MainViewModel
import com.segnities007.hub.HubNavigation
import com.segnities007.navigation.Route
import com.segnities007.ui.theme.CheckMateTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    private var showNotificationRationaleDialog by mutableStateOf(false)

    /**
     * 通知権限リクエスト用のActivityResultLauncher
     * Android 13 (API 33)以降で通知を表示するには、
     * POST_NOTIFICATIONS権限の実行時リクエストが必要
     */
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 権限が許可された場合の処理
                // 必要に応じてログや分析イベントを送信
            } else {
                // 権限が拒否された場合の処理
                // ユーザーに通知機能が制限されることを通知することも検討
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Android 13以降で通知権限をリクエスト
        requestNotificationPermissionIfNeeded()

        setContent {
            CheckMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()

                    // 通知権限の説明ダイアログ
                    if (showNotificationRationaleDialog) {
                        NotificationPermissionRationaleDialog(
                            onConfirm = {
                                showNotificationRationaleDialog = false
                                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            onDismiss = {
                                showNotificationRationaleDialog = false
                            }
                        )
                    }
                }
            }
        }
    }

    /**
     * Android 13 (API 33)以降で通知権限をリクエスト
     * それ以前のバージョンでは何もしない（Manifestの宣言のみで十分）
     */
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 既に権限が許可されている
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // ユーザーが以前に権限を拒否した場合
                    // 理由を説明するダイアログを表示
                    showNotificationRationaleDialog = true
                }
                else -> {
                    // 初回リクエスト
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

/**
 * 通知権限の必要性を説明するダイアログ
 */
@Composable
private fun NotificationPermissionRationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("通知の許可が必要です") },
        text = {
            Text(
                "CheckMateは、未チェックのアイテムがある場合に " +
                    "毎日リマインダー通知を送信します。\n\n" +
                    "通知を受け取ることで、大切なアイテムのチェック忘れを防ぐことができます。"
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("許可する")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("後で")
            }
        }
    )
}

@Composable
private fun MainNavigation() {
    val mainViewModel: MainViewModel = koinInject()
    val state by mainViewModel.state.collectAsState()

    val entryProvider = remember {
        entryProvider {
            entry(Route.Auth) {
                AuthNavigation(
                    topNavigate = { route -> mainViewModel.sendIntent(MainIntent.Navigate(route)) }
                )
            }
            entry(Route.Hub) {
                HubNavigation(
                    onTopNavigate = { route -> mainViewModel.sendIntent(MainIntent.Navigate(route)) }
                )
            }
        }
    }

    NavDisplay(
        backStack = listOf(state.currentRoute),
        entryProvider = entryProvider,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}
