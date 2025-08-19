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
        }
    }

    private fun getAllItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = itemRepository.getAllItems()
            setState { copy(items = items) }
        }
    }

    private fun getItemById(intent: ItemsIntent.GetItemsById) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = itemRepository.getItemById(intent.id)
            if (item != null) {
                setState { copy(items = listOf(item)) }
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
                    if (intent.item.imagePath.isNotBlank()) {
                        imageRepository.saveImage(intent.item.imagePath, "${Uuid.random()}.jpg")
                    }
                    intent.item.copy(imagePath = intent.item.imagePath)
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
}
