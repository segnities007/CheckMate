package com.segnities007.checkmate.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.segnities007.checkmate.MainActivity
import com.segnities007.checkmate.R

/**
 * 通知の作成と管理を行うヘルパークラス
 */
object NotificationHelper {
    private const val CHANNEL_ID = "daily_reminder_channel"
    private const val CHANNEL_NAME = "毎日のリマインダー"
    private const val CHANNEL_DESCRIPTION = "忘れ物チェックのリマインダー通知"
    private const val NOTIFICATION_ID = 1001

    /**
     * 通知チャンネルを作成（Android 8.0以降で必要）
     */
    fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESCRIPTION
            enableVibration(true)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * 忘れ物の通知を表示
     * @param context コンテキスト
     * @param uncheckedCount 未チェックのアイテム数
     */
    fun showDailyReminder(
        context: Context,
        uncheckedCount: Int,
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // アプリを開くインテント
        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        // 通知の内容を作成
        val title = "今日の忘れ物チェック"
        val message =
            when (uncheckedCount) {
                0 -> "すべての持ち物をチェック済みです！"
                1 -> "まだ1個の持ち物が未チェックです"
                else -> "まだ${uncheckedCount}個の持ち物が未チェックです"
            }

        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // アプリのアイコン
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // タップで通知を消す
                .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
