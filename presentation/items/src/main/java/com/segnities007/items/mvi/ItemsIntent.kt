package com.segnities007.items.mvi

import android.net.Uri
import com.segnities007.model.item.BarcodeInfo
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

    data object NavigateToBarcodeScanner : ItemsIntent

    data class BarcodeDetected(
        val barcodeInfo: BarcodeInfo,
    ) : ItemsIntent

    data class GetProductInfo(
        val barcodeInfo: BarcodeInfo,
    ) : ItemsIntent

    data object ClearProductInfo : ItemsIntent
    
    // Reducer-only intents for updating computed/presentation state
    data class SetFilteredItems(
        val filteredItems: List<Item>,
    ) : ItemsIntent

    data class SetItems(
        val items: List<Item>,
    ) : ItemsIntent

    data class SetScannedBarcodeInfo(
        val barcodeInfo: BarcodeInfo?,
    ) : ItemsIntent

    data class SetProductInfoLoading(
        val isLoading: Boolean,
    ) : ItemsIntent

    data class SetProductInfo(
        val productInfo: com.segnities007.model.item.ProductInfo?,
    ) : ItemsIntent

    data class SetShouldClearForm(
        val shouldClear: Boolean,
    ) : ItemsIntent
}
