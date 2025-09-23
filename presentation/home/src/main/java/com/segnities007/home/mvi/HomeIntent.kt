package com.segnities007.home.mvi

import com.segnities007.ui.mvi.MviIntent
import kotlinx.datetime.LocalDate

sealed interface HomeIntent : MviIntent {
    /** 日付を選択したとき、または今日のタスクを取得したいとき */
    data class SelectDate(
        val date: LocalDate,
    ) : HomeIntent

    /** アイテムのチェック状態を変更 */
    data class CheckItem(
        val itemId: Int,
        val checked: Boolean,
    ) : HomeIntent

    /** 全アイテムを読み込む（アプリ起動時など） */
    data object GetAllItem : HomeIntent

    /** 今日のタスクを取得 */
    data object LoadTodayData : HomeIntent

    /** 月間ナビゲーション */
    data class ChangeMonth(
        val year: Int,
        val month: Int,
    ) : HomeIntent

    /** 週ナビゲーション（中心日を指定して移動） */
    data class ChangeWeek(
        val date: LocalDate,
    ) : HomeIntent

    data object EnsureCheckHistory : HomeIntent

    // Reducer-only intents
    data class SetAllItems(
        val allItems: List<com.segnities007.model.item.Item>,
    ) : HomeIntent

    data class SetItemCheckStates(
        val date: LocalDate,
        val itemCheckStates: Map<Int, Boolean>,
    ) : HomeIntent
}
