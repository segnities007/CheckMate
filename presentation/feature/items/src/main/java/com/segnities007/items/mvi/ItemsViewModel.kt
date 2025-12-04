package com.segnities007.items.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.image.DeleteImageUseCase
import com.segnities007.usecase.image.SaveImageUseCase
import com.segnities007.usecase.item.AddItemUseCase
import com.segnities007.usecase.item.DeleteItemUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.item.GetItemByIdUseCase
import com.segnities007.usecase.item.GetProductInfoByBarcodeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class)
class ItemsViewModel(
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val addItemUseCase: AddItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val getProductInfoByBarcodeUseCase: GetProductInfoByBarcodeUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
) : BaseViewModel<ItemsIntent, ItemsState, ItemsEffect>(ItemsState()) {

    init {
        // Start collecting items Flow immediately
        viewModelScope.launch {
            getAllItemsUseCase().collect { items ->
                setState {
                    val newState = copy(items = items)

                    var filtered = items
                    if (searchQuery.isNotBlank()) {
                        filtered = filtered.filter {
                            it.name.contains(
                                searchQuery,
                                ignoreCase = true
                            ) || it.description.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    if (selectedCategory != null) {
                        filtered = filtered.filter { it.category == selectedCategory }
                    }
                    filtered = when (sortOrder) {
                        SortOrder.NAME_ASC -> filtered.sortedBy { it.name }
                        SortOrder.NAME_DESC -> filtered.sortedByDescending { it.name }
                        SortOrder.CREATED_ASC -> filtered.sortedBy { it.createdAt }
                        SortOrder.CREATED_DESC -> filtered.sortedByDescending { it.createdAt }
                        SortOrder.CATEGORY_ASC -> filtered.sortedBy { it.category.name }
                        SortOrder.CATEGORY_DESC -> filtered.sortedByDescending { it.category.name }
                    }
                    newState.copy(filteredItems = filtered)
                }
            }
        }
    }

    override suspend fun handleIntent(intent: ItemsIntent) {
        when (intent) {
            is ItemsIntent.GetAllItems -> {} // No-op, Flow is already collecting
            is ItemsIntent.GetItemsById -> getItemById(intent)
            is ItemsIntent.InsertItems -> insertItem(intent)
            is ItemsIntent.DeleteItems -> deleteItem(intent)
            is ItemsIntent.UpdateCapturedImageUriForBottomSheet -> setState {
                copy(
                    capturedImageUriForBottomSheet = intent.capturedImageUriForBottomSheet
                )
            }

            is ItemsIntent.UpdateCapturedTempPathForViewModel -> setState {
                copy(
                    capturedTempPathForViewModel = intent.capturedTempPathForViewModel
                )
            }

            is ItemsIntent.UpdateIsShowBottomSheet -> setState { copy(isShowBottomSheet = intent.isShowBottomSheet) }
            is ItemsIntent.BarcodeDetected -> handleBarcodeDetected(intent)
            is ItemsIntent.GetProductInfo -> getProductInfo(intent)
            ItemsIntent.ClearProductInfo -> clearProductInfo()
            is ItemsIntent.SetFilteredItems -> setState { copy(filteredItems = intent.filteredItems) }
            is ItemsIntent.SetItems -> setState { copy(items = intent.items) }
            is ItemsIntent.SetScannedBarcodeInfo -> setState { copy(scannedBarcodeInfo = intent.barcodeInfo) }
            is ItemsIntent.SetProductInfoLoading -> setState { copy(isLoadingProductInfo = intent.isLoading) }
            is ItemsIntent.SetProductInfo -> setState { copy(productInfo = intent.productInfo) }
            is ItemsIntent.SetShouldClearForm -> setState { copy(shouldClearForm = intent.shouldClear) }
            is ItemsIntent.UpdateSearchQuery -> updateSearchQuery(intent)
            is ItemsIntent.UpdateSelectedCategory -> updateSelectedCategory(intent)
            is ItemsIntent.UpdateSortOrder -> updateSortOrder(intent)
        }
    }

    private fun updateSearchQuery(intent: ItemsIntent.UpdateSearchQuery) {
        setState { copy(searchQuery = intent.query) }
        applyFilters()
    }

    private fun updateSelectedCategory(intent: ItemsIntent.UpdateSelectedCategory) {
        setState { copy(selectedCategory = intent.category) }
        applyFilters()
    }

    private fun updateSortOrder(intent: ItemsIntent.UpdateSortOrder) {
        setState { copy(sortOrder = intent.sortOrder) }
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = currentState
        var filteredItems = currentState.items

        // 検索フィルタ
        if (currentState.searchQuery.isNotBlank()) {
            filteredItems =
                filteredItems.filter { item ->
                    item.name.contains(currentState.searchQuery, ignoreCase = true) ||
                            item.description.contains(currentState.searchQuery, ignoreCase = true)
                }
        }

        // カテゴリフィルタ
        if (currentState.selectedCategory != null) {
            filteredItems =
                filteredItems.filter { item ->
                    item.category == currentState.selectedCategory
                }
        }

        // 並び替え
        filteredItems =
            when (currentState.sortOrder) {
                SortOrder.NAME_ASC -> filteredItems.sortedBy { it.name }
                SortOrder.NAME_DESC -> filteredItems.sortedByDescending { it.name }
                SortOrder.CREATED_ASC -> filteredItems.sortedBy { it.createdAt }
                SortOrder.CREATED_DESC -> filteredItems.sortedByDescending { it.createdAt }
                SortOrder.CATEGORY_ASC -> filteredItems.sortedBy { it.category.name }
                SortOrder.CATEGORY_DESC -> filteredItems.sortedByDescending { it.category.name }
            }

        setState { copy(filteredItems = filteredItems) }
    }

    private fun getItemById(intent: ItemsIntent.GetItemsById) {
        // Manual handling to support Toast on failure/null
        viewModelScope.launch {
            getItemByIdUseCase(intent.id).fold(
                onSuccess = { item ->
                    if (item != null) {
                        val newItems = listOf(item)
                        setState { copy(items = newItems) }
                        applyFilters()
                    } else {
                        sendEffect { ItemsEffect.ShowToast("アイテムが見つかりません") }
                    }
                },
                onFailure = {
                    sendEffect { ItemsEffect.ShowToast("アイテムの取得に失敗しました") }
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun insertItem(intent: ItemsIntent.InsertItems) {
        viewModelScope.launch {
            val newItem = withContext(Dispatchers.IO) {
                val finalImagePath = if (intent.item.imagePath.isNotBlank()) {
                    if (intent.item.imagePath.startsWith("http")) {
                        intent.item.imagePath
                    } else {
                        saveImageUseCase(intent.item.imagePath, "${Uuid.random()}.jpg").getOrElse {
                            sendEffect { ItemsEffect.ShowToast("画像の保存に失敗しました") }
                            return@withContext null
                        }
                    }
                } else {
                    ""
                }
                intent.item.copy(imagePath = finalImagePath)
            } ?: return@launch

            addItemUseCase(newItem).fold(
                onSuccess = {
                    sendEffect { ItemsEffect.ShowToast("「${newItem.name}」を追加しました") }
                    // Flow will auto-update, no need to manually refresh
                },
                onFailure = { error ->
                    sendEffect {
                        ItemsEffect.ShowToast(
                            error.message ?: "アイテムの追加に失敗しました"
                        )
                    }
                }
            )
        }
    }

    private fun deleteItem(intent: ItemsIntent.DeleteItems) {
        viewModelScope.launch {
            val itemToDelete = getItemByIdUseCase(intent.id).getOrElse {
                sendEffect { ItemsEffect.ShowToast("アイテムの取得に失敗しました") }
                return@launch
            }

            if (itemToDelete == null) {
                sendEffect { ItemsEffect.ShowToast("アイテムが見つかりません") }
                return@launch
            }

            if (itemToDelete.imagePath.isNotBlank()) {
                deleteImageUseCase(itemToDelete.imagePath)
            }

            deleteItemUseCase(intent.id).fold(
                onSuccess = {
                    sendEffect { ItemsEffect.ShowToast("「${itemToDelete.name}」を削除しました") }
                    // Flow will auto-update, no need to manually refresh
                },
                onFailure = { error ->
                    sendEffect { ItemsEffect.ShowToast(error.message ?: "削除に失敗しました") }
                }
            )
        }
    }

    private fun handleBarcodeDetected(intent: ItemsIntent.BarcodeDetected) {
        sendIntent(ItemsIntent.SetScannedBarcodeInfo(intent.barcodeInfo))
        sendIntent(ItemsIntent.GetProductInfo(intent.barcodeInfo))
    }

    private fun getProductInfo(intent: ItemsIntent.GetProductInfo) {
        setState { copy(isLoadingProductInfo = true) }

        viewModelScope.launch {
            getProductInfoByBarcodeUseCase(intent.barcodeInfo).fold(
                onSuccess = { productInfo ->
                    setState { copy(productInfo = productInfo, isLoadingProductInfo = false) }

                    if (productInfo != null) {
                        setState {
                            copy(
                                isShowBottomSheet = false,
                                capturedImageUriForBottomSheet = null,
                                capturedTempPathForViewModel = "",
                                shouldClearForm = true
                            )
                        }
                        sendEffect { ItemsEffect.ReopenBottomSheetWithProductInfo }
                    } else {
                        sendEffect { ItemsEffect.ShowToast("商品情報が見つかりませんでした") }
                    }
                },
                onFailure = { error ->
                    setState { copy(isLoadingProductInfo = false) }
                    sendEffect { ItemsEffect.ShowToast("商品情報の取得に失敗しました: ${error.message}") }
                }
            )
        }
    }

    private fun clearProductInfo() {
        setState {
            copy(
                productInfo = null,
                scannedBarcodeInfo = null,
            )
        }
    }
}
