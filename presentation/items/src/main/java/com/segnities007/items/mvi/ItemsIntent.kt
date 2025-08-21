package com.segnities007.items.mvi

import android.net.Uri
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
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

    data class UpdateIsShowBottomSheet(
        val isShowBottomSheet: Boolean,
    ) : ItemsIntent

    data class UpdateCapturedImageUriForBottomSheet(
        val capturedImageUriForBottomSheet: Uri?,
    ) : ItemsIntent

    data class UpdateCapturedTempPathForViewModel(
        val capturedTempPathForViewModel: String,
    ) : ItemsIntent

    data object NavigateToItemsList : ItemsIntent

    data object NavigateToCameraCapture : ItemsIntent

    data class UpdateSearchQuery(
        val query: String,
    ) : ItemsIntent

    data class UpdateSelectedCategory(
        val category: ItemCategory?,
    ) : ItemsIntent

    data class UpdateSortOrder(
        val sortOrder: SortOrder,
    ) : ItemsIntent
}
