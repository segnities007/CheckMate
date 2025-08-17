package com.segnities007.items.mvi

import com.segnities007.model.Item
import com.segnities007.ui.mvi.MviIntent

sealed interface ItemIntent : MviIntent {
    data object GetAllItems : ItemIntent

    data class GetItemById(
        val id: Int,
    ) : ItemIntent

    data class InsertItem(
        val item: Item,
    ) : ItemIntent

    data class DeleteItem(
        val id: Int,
    ) : ItemIntent
}
