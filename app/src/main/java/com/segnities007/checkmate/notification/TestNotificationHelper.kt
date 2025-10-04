package com.segnities007.checkmate.notification

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * テスト用：すぐに通知を送るヘルパー
 */
object TestNotificationHelper {
    private const val TEST_UNCHECKED_ITEMS_COUNT = 5
    
    /**
     * テスト用：15秒後に通知を送る
     */
    fun scheduleTestNotification(context: Context) {
        val testWorkRequest =
            OneTimeWorkRequestBuilder<DailyReminderWorker>()
                .setInitialDelay(15, TimeUnit.SECONDS)
                .build()

        WorkManager
            .getInstance(context)
            .enqueue(testWorkRequest)
    }

    /**
     * テスト用：即座に通知を表示（Worker経由ではなく直接）
     */
    fun showTestNotificationNow(context: Context) {
        NotificationHelper.showDailyReminder(context, TEST_UNCHECKED_ITEMS_COUNT)
    }
}
