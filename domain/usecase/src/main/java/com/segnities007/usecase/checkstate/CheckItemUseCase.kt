package com.segnities007.usecase.checkstate

import com.segnities007.model.item.ItemCheckRecord
import com.segnities007.model.item.ItemCheckState
import com.segnities007.repository.ItemCheckStateRepository
import kotlinx.datetime.LocalDate

/**
 * アイテムをチェック/アンチェックするUse Case
 * ビジネスロジック: 指定日付のチェック履歴を更新または追加
 */
class CheckItemUseCase(
    private val itemCheckStateRepository: ItemCheckStateRepository
) {
    suspend operator fun invoke(
        itemId: Int,
        date: LocalDate,
        checked: Boolean
    ): Result<Unit> {
        return try {
            // 既存のチェック状態を取得
            val existingState = itemCheckStateRepository.getCheckStateForItem(itemId)
            val newHistory = existingState?.history?.toMutableList() ?: mutableListOf()
            
            // 指定日付の履歴を更新または追加
            val index = newHistory.indexOfFirst { it.date == date }
            if (index >= 0) {
                // 既存の履歴を更新
                newHistory[index] = ItemCheckRecord(date, checked)
            } else {
                // 新しい履歴を追加
                newHistory.add(ItemCheckRecord(date, checked))
            }

            // チェック状態を保存
            itemCheckStateRepository.saveCheckState(
                ItemCheckState(
                    itemId = itemId,
                    history = newHistory,
                    id = existingState?.id ?: 0
                )
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
