package com.segnities007.items.mvi

import com.segnities007.model.Item
import com.segnities007.ui.mvi.MviState

data class ItemState(
    val items: List<Item> = emptyList(),
): MviState