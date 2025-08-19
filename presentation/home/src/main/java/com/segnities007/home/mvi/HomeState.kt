package com.segnities007.home.mvi

import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.item.Item
import com.segnities007.ui.mvi.MviState
import kotlinx.datetime.LocalDate

data class HomeState(
    val selectedDate: LocalDate = LocalDate.parse("1970-01-01"), // デフォルト値
    val templatesForToday: List<WeeklyTemplate> = emptyList(),
    val itemsForToday: List<Item> = emptyList(),
    val itemCheckStates: Map<Int, Boolean> = emptyMap(), // Compose に渡すチェック状態
    val allItem: List<Item> = emptyList(),
) : MviState {
    /**
     * チェック状態を更新して新しい HomeState を返すユーティリティ
     */
    fun updateCheckState(
        itemId: Int,
        checked: Boolean,
    ): HomeState {
        val newMap = itemCheckStates.toMutableMap()
        newMap[itemId] = checked
        return copy(itemCheckStates = newMap)
    }
}
