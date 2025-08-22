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
}
