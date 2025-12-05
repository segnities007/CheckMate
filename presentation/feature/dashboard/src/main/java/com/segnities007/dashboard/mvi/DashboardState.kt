package com.segnities007.dashboard.mvi

import com.segnities007.model.item.Item // 正しいインポートパス
import com.segnities007.ui.mvi.MviState

data class DashboardState(
    val itemCount: Int = 0,
    val templateCount: Int = 0,
    val uncheckedItemsToday: List<Item> = emptyList(),
    val uncheckedItemsTomorrow: List<Item> = emptyList(),
    val scheduledItemCountToday: Int = 0,
    val checkedItemCountToday: Int = 0,
    val completionRateToday: Int = 0,
    val totalRecordsCount: Int = 0,
    val totalCheckedRecordsCount: Int = 0,
    val historicalCompletionRate: Int = 0,
    // TODO: 「忘れ物の統計」に関する状態を後で追加
) : MviState
