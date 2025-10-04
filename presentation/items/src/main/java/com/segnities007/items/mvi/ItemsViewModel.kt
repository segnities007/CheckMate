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
    private val reducer: ItemsReducer = ItemsReducer()

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
            is ItemsIntent.SetFilteredItems -> setState { reducer.reduce(this, intent) }
            is ItemsIntent.SetItems -> setState { reducer.reduce(this, intent) }
            is ItemsIntent.SetScannedBarcodeInfo -> setState { reducer.reduce(this, intent) }
            is ItemsIntent.SetProductInfoLoading -> setState { reducer.reduce(this, intent) }
            is ItemsIntent.SetProductInfo -> setState { reducer.reduce(this, intent) }
            is ItemsIntent.SetShouldClearForm -> setState { reducer.reduce(this, intent) }
            is ItemsIntent.UpdateSearchQuery -> updateSearchQuery(intent)
            is ItemsIntent.UpdateSelectedCategory -> updateSelectedCategory(intent)
            is ItemsIntent.UpdateSortOrder -> updateSortOrder(intent)
        }
    }

    private fun updateIsShowBottomSheet(intent: ItemsIntent.UpdateIsShowBottomSheet) {
        setState { reducer.reduce(this, intent) }
    }

    private fun updateCapturedImageUriForBottomSheet(intent: ItemsIntent.UpdateCapturedImageUriForBottomSheet) {
        setState { reducer.reduce(this, intent) }
    }

    private fun updateCapturedTempPathForViewModel(intent: ItemsIntent.UpdateCapturedTempPathForViewModel) {
        setState { reducer.reduce(this, intent) }
    }

    private fun updateSearchQuery(intent: ItemsIntent.UpdateSearchQuery) {
        setState { reducer.reduce(this, intent) }
        applyFilters()
    }

    private fun updateSelectedCategory(intent: ItemsIntent.UpdateSelectedCategory) {
        setState { reducer.reduce(this, intent) }
        applyFilters()
    }

    private fun updateSortOrder(intent: ItemsIntent.UpdateSortOrder) {
        setState { reducer.reduce(this, intent) }
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

        setState { reducer.reduce(this, ItemsIntent.SetFilteredItems(filteredItems)) }
    }

    private suspend fun getAllItems() {
        val items = getAllItemsUseCase()
        // apply state directly via reducer in the current coroutine
        setState { reducer.reduce(this, ItemsIntent.SetItems(items)) }
        applyFilters()
    }

    private suspend fun getItemById(intent: ItemsIntent.GetItemsById) {
        val item = getItemByIdUseCase(intent.id)
        if (item != null) {
            // set items via reducer directly
            setState { reducer.reduce(this, ItemsIntent.SetItems(listOf(item))) }
            applyFilters()
        } else {
            sendEffect { ItemsEffect.ShowToast("アイテムが見つかりません") }
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private suspend fun insertItem(intent: ItemsIntent.InsertItems) {
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
                            val result = saveImageUseCase(intent.item.imagePath, "${Uuid.random()}.jpg")
                            result.getOrElse { "" }
                        }
                    } else {
                        ""
                    }
                intent.item.copy(imagePath = finalImagePath)
            }
        
        val result = addItemUseCase(newItem)
        result.fold(
            onSuccess = {
                sendEffect { ItemsEffect.ShowToast("「${newItem.name}」を追加しました") }
                // refresh
                getAllItems()
            },
            onFailure = { error ->
                sendEffect { ItemsEffect.ShowToast(error.message ?: "アイテムの追加に失敗しました") }
            }
        )
    }

    private suspend fun deleteItem(intent: ItemsIntent.DeleteItems) {
        val itemToDelete = getItemByIdUseCase(intent.id)
        if (itemToDelete != null) {
            // 画像を削除
            if (itemToDelete.imagePath.isNotBlank()) {
                deleteImageUseCase(itemToDelete.imagePath)
            }
            
            // アイテムを削除
            val result = deleteItemUseCase(intent.id)
            result.fold(
                onSuccess = {
                    sendEffect { ItemsEffect.ShowToast("「${itemToDelete.name}」を削除しました") }
                    // refresh
                    getAllItems()
                },
                onFailure = { error ->
                    sendEffect { ItemsEffect.ShowToast(error.message ?: "削除に失敗しました") }
                }
            )
        } else {
            sendEffect { ItemsEffect.ShowToast("削除対象のアイテムが見つかりません") }
        }
    }

    private fun handleBarcodeDetected(intent: ItemsIntent.BarcodeDetected) {
        // update scanned barcode info via reducer
        sendIntent(ItemsIntent.SetScannedBarcodeInfo(intent.barcodeInfo))
        // バーコード検出後、自動的に商品情報を取得
        sendIntent(ItemsIntent.GetProductInfo(intent.barcodeInfo))
    }

    private suspend fun getProductInfo(intent: ItemsIntent.GetProductInfo) {
        // set loading via reducer
        setState { reducer.reduce(this, ItemsIntent.SetProductInfoLoading(true)) }
        
        val result = getProductInfoByBarcodeUseCase(intent.barcodeInfo)
        result.fold(
            onSuccess = { productInfo ->
                // set product info and loading flag via reducer
                setState { reducer.reduce(this, ItemsIntent.SetProductInfo(productInfo)) }
                setState { reducer.reduce(this, ItemsIntent.SetProductInfoLoading(false)) }

                if (productInfo != null) {
                    // 商品情報が取得できた場合、ボトムシートの状態を reset via reducer
                    setState { reducer.reduce(this, ItemsIntent.UpdateIsShowBottomSheet(false)) }
                    setState { reducer.reduce(this, ItemsIntent.UpdateCapturedImageUriForBottomSheet(null)) }
                    setState { reducer.reduce(this, ItemsIntent.UpdateCapturedTempPathForViewModel("")) }
                    setState { reducer.reduce(this, ItemsIntent.SetShouldClearForm(true)) }

                    // 少し遅延を入れてからボトムシートを表示（状態リセットのため）
                    kotlinx.coroutines.delay(100)
                    setState { reducer.reduce(this, ItemsIntent.UpdateIsShowBottomSheet(true)) }

                    // フォームクリアフラグをリセット（productInfoは保持）
                    kotlinx.coroutines.delay(200)
                    setState { reducer.reduce(this, ItemsIntent.SetShouldClearForm(false)) }
                } else {
                    sendEffect { ItemsEffect.ShowToast("商品情報が見つかりませんでした") }
                }
            },
            onFailure = { error ->
                setState { reducer.reduce(this, ItemsIntent.SetProductInfoLoading(false)) }
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
