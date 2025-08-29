package com.segnities007.items.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.repository.ImageRepository
import com.segnities007.repository.ItemRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ItemsViewModel(
    private val itemRepository: ItemRepository,
    private val imageRepository: ImageRepository,
) : BaseViewModel<ItemsIntent, ItemsState, ItemsEffect>(ItemsState()),
    KoinComponent {
    init {
        sendIntent(ItemsIntent.GetAllItems)
    }

    override suspend fun handleIntent(intent: ItemsIntent) {
        when (intent) {
            is ItemsIntent.GetAllItems -> getAllItems()
            is ItemsIntent.GetItemsById -> getItemById(intent)
            is ItemsIntent.InsertItems -> insertItem(intent)
            is ItemsIntent.DeleteItems -> deleteItem(intent)
            is ItemsIntent.UpdateCapturedImageUriForBottomSheet -> updateCapturedImageUriForBottomSheet(intent)
            is ItemsIntent.UpdateCapturedTempPathForViewModel -> updateCapturedTempPathForViewModel(intent)
            is ItemsIntent.UpdateIsShowBottomSheet -> updateIsShowBottomSheet(intent)
            ItemsIntent.NavigateToItemsList -> sendEffect { ItemsEffect.NavigateToItemsList }
            ItemsIntent.NavigateToCameraCapture -> sendEffect { ItemsEffect.NavigateToCameraCapture }
            ItemsIntent.NavigateToBarcodeScanner -> sendEffect { ItemsEffect.NavigateToBarcodeScanner }
            is ItemsIntent.BarcodeDetected -> handleBarcodeDetected(intent)
            is ItemsIntent.GetProductInfo -> getProductInfo(intent)
            ItemsIntent.ClearProductInfo -> clearProductInfo()
            is ItemsIntent.UpdateSearchQuery -> updateSearchQuery(intent)
            is ItemsIntent.UpdateSelectedCategory -> updateSelectedCategory(intent)
            is ItemsIntent.UpdateSortOrder -> updateSortOrder(intent)
        }
    }

    private fun updateIsShowBottomSheet(intent: ItemsIntent.UpdateIsShowBottomSheet) {
        setState { copy(isShowBottomSheet = intent.isShowBottomSheet) }
    }

    private fun updateCapturedImageUriForBottomSheet(intent: ItemsIntent.UpdateCapturedImageUriForBottomSheet) {
        setState { copy(capturedImageUriForBottomSheet = intent.capturedImageUriForBottomSheet) }
    }

    private fun updateCapturedTempPathForViewModel(intent: ItemsIntent.UpdateCapturedTempPathForViewModel) {
        setState { copy(capturedTempPathForViewModel = intent.capturedTempPathForViewModel) }
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

    @OptIn(ExperimentalTime::class)
    private fun applyFilters() {
        val currentState = state.value
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

    private fun getAllItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = itemRepository.getAllItems()
            setState { copy(items = items) }
            applyFilters()
        }
    }

    private fun getItemById(intent: ItemsIntent.GetItemsById) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = itemRepository.getItemById(intent.id)
            if (item != null) {
                setState { copy(items = listOf(item)) }
                applyFilters()
            } else {
                sendEffect { ItemsEffect.ShowToast("アイテムが見つかりません") }
            }
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private fun insertItem(intent: ItemsIntent.InsertItems) {
        viewModelScope.launch {
            if (intent.item.name.isBlank()) {
                sendEffect { ItemsEffect.ShowToast("アイテム名を入力してください") }
                return@launch
            }
            val newItem =
                withContext(Dispatchers.IO) {
                    val finalImagePath =
                        if (intent.item.imagePath.isNotBlank()) {
                            // URLの場合はそのまま使用、ローカルファイルパスの場合は保存
                            if (intent.item.imagePath.startsWith("http://") || intent.item.imagePath.startsWith("https://")) {
                                android.util.Log.d("ItemsViewModel", "Using URL as imagePath: ${intent.item.imagePath}")
                                intent.item.imagePath
                            } else {
                                android.util.Log.d("ItemsViewModel", "Saving local image: ${intent.item.imagePath}")
                                imageRepository.saveImage(intent.item.imagePath, "${Uuid.random()}.jpg")
                            }
                        } else {
                            ""
                        }
                    intent.item.copy(imagePath = finalImagePath)
                }
            itemRepository.insertItem(newItem)
            sendEffect { ItemsEffect.ShowToast("「${newItem.name}」を追加しました") }
            getAllItems()
        }
    }

    private fun deleteItem(intent: ItemsIntent.DeleteItems) {
        viewModelScope.launch(Dispatchers.IO) {
            val itemToDelete = itemRepository.getItemById(intent.id)
            if (itemToDelete != null) {
                itemToDelete.imagePath?.let { imageRepository.deleteImage(it) }
                itemRepository.deleteItem(intent.id)
                sendEffect { ItemsEffect.ShowToast("「${itemToDelete.name}」を削除しました") }
            } else {
                sendEffect { ItemsEffect.ShowToast("削除対象のアイテムが見つかりません") }
            }
            getAllItems()
        }
    }

    private fun handleBarcodeDetected(intent: ItemsIntent.BarcodeDetected) {
        setState { copy(scannedBarcodeInfo = intent.barcodeInfo) }
        // バーコード検出後、自動的に商品情報を取得
        sendIntent(ItemsIntent.GetProductInfo(intent.barcodeInfo))
    }

    private fun getProductInfo(intent: ItemsIntent.GetProductInfo) {
        viewModelScope.launch {
            setState { copy(isLoadingProductInfo = true) }
            try {
                val productInfo = itemRepository.getProductInfoByBarcode(intent.barcodeInfo)
                setState {
                    copy(
                        productInfo = productInfo,
                        isLoadingProductInfo = false,
                    )
                }

                if (productInfo != null) {
                    // 商品情報が取得できた場合、ボトムシートの状態をリセットしてから表示
                    setState {
                        copy(
                            isShowBottomSheet = false,
                            capturedImageUriForBottomSheet = null,
                            capturedTempPathForViewModel = "",
                            shouldClearForm = true,
                        )
                    }

                    // 少し遅延を入れてからボトムシートを表示（状態リセットのため）
                    kotlinx.coroutines.delay(100)
                    setState { copy(isShowBottomSheet = true) }

                    // フォームクリアフラグをリセット（productInfoは保持）
                    kotlinx.coroutines.delay(200)
                    setState { copy(shouldClearForm = false) }
                } else {
                    sendEffect { ItemsEffect.ShowToast("商品情報が見つかりませんでした") }
                }
            } catch (e: Exception) {
                setState { copy(isLoadingProductInfo = false) }
                sendEffect { ItemsEffect.ShowToast("商品情報の取得に失敗しました: ${e.message}") }
            }
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
