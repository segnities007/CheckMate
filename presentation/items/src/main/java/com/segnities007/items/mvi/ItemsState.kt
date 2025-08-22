package com.segnities007.items.mvi

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.segnities007.model.item.BarcodeInfo
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.model.item.ProductInfo
import com.segnities007.ui.mvi.MviState

data class ItemsState(
    val items: List<Item> = emptyList(),
    val isShowBottomSheet: Boolean = false,
    val capturedImageUriForBottomSheet: Uri? = null,
    val capturedTempPathForViewModel: String = "",
    val searchQuery: String = "",
    val selectedCategory: ItemCategory? = null,
    val sortOrder: SortOrder = SortOrder.NAME_ASC,
    val filteredItems: List<Item> = emptyList(),
    val scannedBarcodeInfo: BarcodeInfo? = null,
    val productInfo: ProductInfo? = null,
    val isLoadingProductInfo: Boolean = false,
    val shouldClearForm: Boolean = false,
) : MviState

enum class SortOrder {
    NAME_ASC,
    NAME_DESC,
    CREATED_ASC,
    CREATED_DESC,
    CATEGORY_ASC,
    CATEGORY_DESC,
}