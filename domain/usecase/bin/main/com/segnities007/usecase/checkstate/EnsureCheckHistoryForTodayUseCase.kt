package com.segnities007.usecase.checkstate

import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.ItemCheckStateRepository
import com.segnities007.repository.ItemRepository
import com.segnities007.repository.WeeklyTemplateRepository
import kotlinx.datetime.LocalDate

/**
 * 今日のチェック履歴を確保するUse Case
 * ビジネスロジック: 今日スケジュールされているアイテムに対して、
 * チェック履歴が存在しない場合は未チェック状態で作成する
 */
import kotlinx.coroutines.flow.first

class EnsureCheckHistoryForTodayUseCase(
    private val itemRepository: ItemRepository,
    private val templateRepository: WeeklyTemplateRepository,
    private val checkStateRepository: ItemCheckStateRepository
) {
    suspend operator fun invoke(today: LocalDate): Result<Unit> {
        return try {
            // 今日のテンプレートを取得
            val templatesForToday = templateRepository.getTemplatesForDay(today.dayOfWeek.name)
            val itemIdsForToday = templatesForToday.flatMap { it.itemIds }.distinct()
            
            // 全アイテムを取得してフィルタリング
            val allItems = itemRepository.getAllItems().first()
            val itemsScheduledForToday = allItems.filter { itemIdsForToday.contains(it.id) }

            // 各アイテムのチェック履歴を確認・作成
            for (item in itemsScheduledForToday) {
                val existingState = checkStateRepository.getCheckStateForItem(item.id)
                
                if (existingState == null) {
                    // チェック状態が存在しない場合は作成
                    val newCheckState = ItemCheckState(
                        itemId = item.id,
                        history = mutableListOf(ItemCheckRecord(date = today, isChecked = false))
                    )
                    checkStateRepository.saveCheckState(newCheckState)
                } else {
                    // 今日の履歴が存在しない場合は追加
                    val todayRecord = existingState.history.find { it.date == today }
                    if (todayRecord == null) {
                        val updatedHistory = existingState.history.toMutableList().apply {
                            add(ItemCheckRecord(date = today, isChecked = false))
                        }
                        val updatedCheckState = existingState.copy(history = updatedHistory)
                        checkStateRepository.saveCheckState(updatedCheckState)
                    }
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
