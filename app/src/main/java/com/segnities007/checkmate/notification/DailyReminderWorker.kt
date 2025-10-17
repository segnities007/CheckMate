package com.segnities007.checkmate.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.segnities007.model.DayOfWeek
import com.segnities007.repository.ItemCheckStateRepository
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.WeeklyTemplateRepository
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.ExperimentalTime

/**
 * 毎朝7:30に実行されるWorker
 * 今日の未チェックアイテム数を計算して通知を送る
 */
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params),
    KoinComponent {
    private val itemRepository: ItemRepository by inject()
    private val weeklyTemplateRepository: WeeklyTemplateRepository by inject()
    private val itemCheckStateRepository: ItemCheckStateRepository by inject()

    @OptIn(ExperimentalTime::class)
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "DailyReminderWorker started")
            
            // 今日の日付と曜日を取得
            val today =
                kotlin.time.Clock.System
                    .now()
                    .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                    .date
            val todayDayOfWeek = convertToDomainDayOfWeek(today.dayOfWeek)
            
            Log.d(TAG, "Today: $today, DayOfWeek: $todayDayOfWeek")

            // 今日のテンプレートを取得
            val todaysTemplates = weeklyTemplateRepository.getTemplatesForDay(todayDayOfWeek.name)
            Log.d(TAG, "Today's templates count: ${todaysTemplates.size}")

            // 今日必要なアイテムIDのリストを取得
            val todaysItemIds = todaysTemplates.flatMap { it.itemIds }.distinct()
            Log.d(TAG, "Today's item IDs: $todaysItemIds")

            if (todaysItemIds.isEmpty()) {
                Log.d(TAG, "No items for today, skipping notification")
                // 今日のアイテムがない場合は通知を送らない
                return Result.success()
            }

            // すべてのアイテムを取得
            val allItems = itemRepository.getAllItems()

            // 今日のアイテムのみをフィルタリング
            val todaysItems = allItems.filter { it.id in todaysItemIds }

            // チェック状態を取得
            val checkStates = itemCheckStateRepository.getCheckStatesForItems(todaysItemIds)

            // 今日の日付でチェックされているかを確認
            val checkedItemIds =
                checkStates
                    .filter { state ->
                        state.history.any { record ->
                            record.date == today && record.isChecked
                        }
                    }.map { it.itemId }
                    .toSet()

            // 未チェックのアイテム数を計算
            val uncheckedCount = todaysItems.count { it.id !in checkedItemIds }
            Log.d(TAG, "Unchecked items count: $uncheckedCount")

            // 通知を表示
            NotificationHelper.showDailyReminder(applicationContext, uncheckedCount)
            Log.d(TAG, "Notification sent successfully")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in DailyReminderWorker", e)
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "DailyReminderWorker"
    }

    /**
     * kotlinx.datetime.DayOfWeekをドメインのDayOfWeekに変換
     */
    private fun convertToDomainDayOfWeek(dayOfWeek: kotlinx.datetime.DayOfWeek): DayOfWeek =
        when (dayOfWeek) {
            kotlinx.datetime.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
            kotlinx.datetime.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
            kotlinx.datetime.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
            kotlinx.datetime.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
            kotlinx.datetime.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
            kotlinx.datetime.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
            kotlinx.datetime.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
        }
}
