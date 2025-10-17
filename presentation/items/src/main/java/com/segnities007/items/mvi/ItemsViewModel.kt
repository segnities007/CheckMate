package com.segnities007.items.mvi
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.image.DeleteImageUseCase
import com.segnities007.usecase.image.SaveImageUseCase
import com.segnities007.usecase.item.AddItemUseCase
import com.segnities007.usecase.item.DeleteItemUseCase
import com.segnities007.usecase.item.GetAllItemsUseCase
import com.segnities007.usecase.item.GetItemByIdUseCase
import com.segnities007.usecase.item.GetProductInfoByBarcodeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ItemsViewModel(
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val addItemUseCase: AddItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val getProductInfoByBarcodeUseCase: GetProductInfoByBarcodeUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
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
            is ItemsIntent.SetFilteredItems -> setState { reduce(intent) }
            is ItemsIntent.SetItems -> setState { reduce(intent) }
            is ItemsIntent.SetScannedBarcodeInfo -> setState { reduce(intent) }
            is ItemsIntent.SetProductInfoLoading -> setState { reduce(intent) }
            is ItemsIntent.SetProductInfo -> setState { reduce(intent) }
            is ItemsIntent.SetShouldClearForm -> setState { reduce(intent) }
            is ItemsIntent.UpdateSearchQuery -> updateSearchQuery(intent)
            is ItemsIntent.UpdateSelectedCategory -> updateSelectedCategory(intent)
            is ItemsIntent.UpdateSortOrder -> updateSortOrder(intent)
        }
    }

    private fun updateIsShowBottomSheet(intent: ItemsIntent.UpdateIsShowBottomSheet) {
        setState { reduce(intent) }
    }

    private fun updateCapturedImageUriForBottomSheet(intent: ItemsIntent.UpdateCapturedImageUriForBottomSheet) {
        setState { reduce(intent) }
    }

    private fun updateCapturedTempPathForViewModel(intent: ItemsIntent.UpdateCapturedTempPathForViewModel) {
        setState { reduce(intent) }
    }

    private fun updateSearchQuery(intent: ItemsIntent.UpdateSearchQuery) {
        setState { reduce(intent) }
        applyFilters()
    }

    private fun updateSelectedCategory(intent: ItemsIntent.UpdateSelectedCategory) {
        setState { reduce(intent) }
        applyFilters()
    }

    private fun updateSortOrder(intent: ItemsIntent.UpdateSortOrder) {
        setState { reduce(intent) }
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

        setState { reduce(ItemsIntent.SetFilteredItems(filteredItems)) }
    }

    private suspend fun getAllItems() {
        getAllItemsUseCase().fold(
            onSuccess = { items ->
                setState { reduce(ItemsIntent.SetItems(items)) }
                applyFilters()
            },
            onFailure = { e ->
                sendEffect { ItemsEffect.ShowToast("アイテムの読み込みに失敗しました") }
            }
        )
    }

    private suspend fun getItemById(intent: ItemsIntent.GetItemsById) {
        getItemByIdUseCase(intent.id).fold(
            onSuccess = { item ->
                if (item != null) {
                    setState { reduce(ItemsIntent.SetItems(listOf(item))) }
                    applyFilters()
                } else {
                    sendEffect { ItemsEffect.ShowToast("アイテムが見つかりません") }
                }
            },
            onFailure = { e ->
                sendEffect { ItemsEffect.ShowToast("アイテムの取得に失敗しました") }
            }
        )
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private suspend fun insertItem(intent: ItemsIntent.InsertItems) {
        val newItem =
            withContext(Dispatchers.IO) {
                val finalImagePath =
                    if (intent.item.imagePath.isNotBlank()) {
                        if (intent.item.imagePath.startsWith("http://") || intent.item.imagePath.startsWith("https://")) {
                            intent.item.imagePath
                        } else {
                            val result = saveImageUseCase(intent.item.imagePath, "${Uuid.random()}.jpg")
                            result.getOrElse { e ->
                                sendEffect { ItemsEffect.ShowToast("画像の保存に失敗しました") }
                                return@withContext null
                            }
                        }
                    } else {
                        ""
                    }
                intent.item.copy(imagePath = finalImagePath)
            }

        if (newItem == null) {
            return
        }
        
        val result = addItemUseCase(newItem)
        result.fold(
            onSuccess = {
                sendEffect { ItemsEffect.ShowToast("「${newItem.name}」を追加しました") }
                getAllItems()
            },
            onFailure = { error ->
                sendEffect { ItemsEffect.ShowToast(error.message ?: "アイテムの追加に失敗しました") }
            }
        )
    }

    private suspend fun deleteItem(intent: ItemsIntent.DeleteItems) {
        val itemToDelete = getItemByIdUseCase(intent.id).getOrElse { error ->
            sendEffect { ItemsEffect.ShowToast(error.message ?: "アイテムの取得に失敗しました") }
            return
        }

        if (itemToDelete == null) {
            sendEffect { ItemsEffect.ShowToast("アイテムが見つかりません") }
            return
        }

        if (itemToDelete.imagePath.isNotBlank()) {
            deleteImageUseCase(itemToDelete.imagePath).getOrElse { }
        }

        deleteItemUseCase(intent.id).getOrElse { error ->
            sendEffect { ItemsEffect.ShowToast(error.message ?: "削除に失敗しました") }
            return
        }

        sendEffect { ItemsEffect.ShowToast("「${itemToDelete.name}」を削除しました") }
        getAllItems()
    }

    private fun handleBarcodeDetected(intent: ItemsIntent.BarcodeDetected) {
        sendIntent(ItemsIntent.SetScannedBarcodeInfo(intent.barcodeInfo))
        sendIntent(ItemsIntent.GetProductInfo(intent.barcodeInfo))
    }

    private suspend fun getProductInfo(intent: ItemsIntent.GetProductInfo) {
        setState { reduce(ItemsIntent.SetProductInfoLoading(true)) }
        
        val result = getProductInfoByBarcodeUseCase(intent.barcodeInfo)
        result.fold(
            onSuccess = { productInfo ->
                setState { reduce(ItemsIntent.SetProductInfo(productInfo)) }
                setState { reduce(ItemsIntent.SetProductInfoLoading(false)) }

                if (productInfo != null) {
                    setState { reduce(ItemsIntent.UpdateIsShowBottomSheet(false)) }
                    setState { reduce(ItemsIntent.UpdateCapturedImageUriForBottomSheet(null)) }
                    setState { reduce(ItemsIntent.UpdateCapturedTempPathForViewModel("")) }
                    setState { reduce(ItemsIntent.SetShouldClearForm(true)) }
                    sendEffect { ItemsEffect.ReopenBottomSheetWithProductInfo }
                } else {
                    sendEffect { ItemsEffect.ShowToast("商品情報が見つかりませんでした") }
                }
            },
            onFailure = { error ->
                setState { reduce(ItemsIntent.SetProductInfoLoading(false)) }
                sendEffect { ItemsEffect.ShowToast("商品情報の取得に失敗しました: ${error.message}") }
            }
        )
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

// =============================================================================
// Reducer Function
// =============================================================================

private fun ItemsState.reduce(intent: ItemsIntent): ItemsState {
    return when (intent) {
        // ボトムシート関連
        is ItemsIntent.UpdateIsShowBottomSheet -> copy(isShowBottomSheet = intent.isShowBottomSheet)
        is ItemsIntent.UpdateCapturedImageUriForBottomSheet -> copy(capturedImageUriForBottomSheet = intent.capturedImageUriForBottomSheet)
        is ItemsIntent.UpdateCapturedTempPathForViewModel -> copy(capturedTempPathForViewModel = intent.capturedTempPathForViewModel)
        
        // 検索・フィルター関連
        is ItemsIntent.UpdateSearchQuery -> copy(searchQuery = intent.query)
        is ItemsIntent.UpdateSelectedCategory -> copy(selectedCategory = intent.category)
        is ItemsIntent.UpdateSortOrder -> copy(sortOrder = intent.sortOrder)
        is ItemsIntent.SetFilteredItems -> copy(filteredItems = intent.filteredItems)
        
        // アイテム関連
        is ItemsIntent.SetItems -> copy(items = intent.items)
        
        // バーコード・商品情報関連
        is ItemsIntent.SetScannedBarcodeInfo -> copy(scannedBarcodeInfo = intent.barcodeInfo)
        is ItemsIntent.SetProductInfoLoading -> copy(isLoadingProductInfo = intent.isLoading)
        is ItemsIntent.SetProductInfo -> copy(productInfo = intent.productInfo)
        is ItemsIntent.SetShouldClearForm -> copy(shouldClearForm = intent.shouldClear)
        
        // 他のIntentはViewModelで処理（非同期処理など）
        else -> this
    }
}
