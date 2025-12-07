package com.segnities007.checkmate.notification

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * 毎日の通知をスケジュールするクラス
 */
object NotificationScheduler {
    private const val TAG = "NotificationScheduler"
    private const val WORK_NAME = "daily_reminder_work"
    private const val TARGET_HOUR = 7 // 朝7時
    private const val TARGET_MINUTE = 30 // 30分

    /**
     * 毎日7:30に実行される通知をスケジュール
     */
    fun scheduleDailyReminder(context: Context) {
        // 次回の実行時刻を計算
        val initialDelay = calculateInitialDelay()
        val delayHours = initialDelay / (1000 * 60 * 60)
        val delayMinutes = (initialDelay / (1000 * 60)) % 60
        
        Log.d(TAG, "Scheduling daily reminder with initial delay: $delayHours hours $delayMinutes minutes")

        // 毎日実行されるWorkRequestを作成
        val dailyWorkRequest =
            PeriodicWorkRequestBuilder<DailyReminderWorker>(
                1,
                TimeUnit.DAYS,
            )
                // .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

        // WorkManagerにスケジュール（既存のものがあれば置き換え）
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                dailyWorkRequest,
            )
        
        Log.d(TAG, "Daily reminder scheduled successfully")
    }

    /**
     * 次回の7:30までの遅延時間を計算（ミリ秒）
     */
    private fun calculateInitialDelay(): Long {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance()

        // 今日の7:30に設定
        targetTime.set(Calendar.HOUR_OF_DAY, TARGET_HOUR)
        targetTime.set(Calendar.MINUTE, TARGET_MINUTE)
        targetTime.set(Calendar.SECOND, 0)
        targetTime.set(Calendar.MILLISECOND, 0)

        // もし現在時刻が7:30を過ぎていたら、明日の7:30に設定
        if (currentTime.timeInMillis > targetTime.timeInMillis) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 現在時刻から次回の7:30までのミリ秒を返す
        return targetTime.timeInMillis - currentTime.timeInMillis
    }

    /**
     * スケジュールされた通知をキャンセル
     */
    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
