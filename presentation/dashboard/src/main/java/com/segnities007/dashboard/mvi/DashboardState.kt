package com.segnities007.dashboard.mvi

import com.segnities007.model.item.Item // 正しいインポートパス
import com.segnities007.ui.mvi.MviState

data class DashboardState(
    val isLoading: Boolean = false,
    val itemCount: Int = 0,
    val templateCount: Int = 0,
    val uncheckedItemsToday: List<Item> = emptyList(),
    // TODO: 「忘れ物の統計」に関する状態を後で追加
    val error: String? = null
): MviState
