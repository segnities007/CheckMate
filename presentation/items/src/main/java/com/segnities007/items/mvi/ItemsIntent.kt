package com.segnities007.items.mvi

import com.segnities007.model.Item
import com.segnities007.ui.mvi.MviIntent

sealed interface ItemsIntent : MviIntent {
    data object GetAllItems : ItemsIntent

    data class GetItemsById(
        val id: Int,
    ) : ItemsIntent

    data class InsertItems(
        val item: Item,
    ) : ItemsIntent

    data class DeleteItems(
        val id: Int,
    ) : ItemsIntent
}
