package com.segnities007.checkmate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.segnities007.auth.AuthNavigation
import com.segnities007.checkmate.mvi.MainEffect
import com.segnities007.checkmate.mvi.MainIntent
import com.segnities007.checkmate.mvi.MainViewModel
import com.segnities007.ui.theme.CheckMateTheme
import com.segnities007.hub.HubNavigation
import com.segnities007.navigation.Route
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

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
                MainNavigation()
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
                    // 理由を説明するダイアログを表示することを推奨
                    // ここでは直接リクエストを実行
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // 初回リクエスト
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

@Composable
private fun MainNavigation() {
    val mainNavController = rememberNavController()
    val mainViewModel: MainViewModel = koinInject()

    val onNavigate: (Route) -> Unit = { route ->
        mainViewModel.sendIntent(MainIntent.Navigate(route))
    }

    LaunchedEffect(Unit) {
        mainViewModel.effect.collect {
            when (it) {
                is MainEffect.Navigate -> mainNavController.navigate(it.route)
            }
        }
    }

    NavHost(
        navController = mainNavController,
        startDestination = Route.Auth,
    ) {
        composable<Route.Auth> {
            AuthNavigation(onNavigate)
        }
        composable<Route.Hub> {
            HubNavigation(onNavigate)
        }
    }
}
