package com.segnities007.items.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.model.Item
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
) : BaseViewModel<ItemIntent, ItemState, ItemEffect>(ItemState()),
    KoinComponent {
    init {
        sendIntent(ItemIntent.GetAllItems)
    }

    override suspend fun handleIntent(intent: ItemIntent) {
        when (intent) {
            is ItemIntent.GetAllItems -> getAllItems()
            is ItemIntent.GetItemById -> getItemById(intent)
            is ItemIntent.InsertItem -> insertItem(intent)
            is ItemIntent.DeleteItem -> deleteItem(intent)
        }
    }

    private fun getAllItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = itemRepository.getAllItems()
            setState { copy(items = items) }
        }
    }

    private fun getItemById(intent: ItemIntent.GetItemById) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = itemRepository.getItemById(intent.id)
            if (item != null) {
                setState { copy(items = listOf(item)) }
            } else {
                sendEffect { ItemEffect.ShowToast("アイテムが見つかりません") }
            }
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private fun insertItem(intent: ItemIntent.InsertItem) {
        viewModelScope.launch {
            if (intent.item.name.isBlank()) {
                sendEffect { ItemEffect.ShowToast("アイテム名を入力してください") }
                return@launch
            }
            val newItem =
                withContext(Dispatchers.IO) {
                    val savedImagePath =
                        intent.item.imagePath?.let { path ->
                            imageRepository.saveImage(path, "${Uuid.random()}.jpg")
                        }
                    intent.item.copy(imagePath = savedImagePath)
                }
            itemRepository.insertItem(newItem)
            sendEffect { ItemEffect.ShowToast("「${newItem.name}」を追加しました") }
            getAllItems()
        }
    }

    private fun deleteItem(intent: ItemIntent.DeleteItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val itemToDelete = itemRepository.getItemById(intent.id)
            if (itemToDelete != null) {
                itemToDelete.imagePath?.let { imageRepository.deleteImage(it) }
                itemRepository.deleteItem(intent.id)
                sendEffect { ItemEffect.ShowToast("「${itemToDelete.name}」を削除しました") }
            } else {
                sendEffect { ItemEffect.ShowToast("削除対象のアイテムが見つかりません") }
            }
            getAllItems()
        }
    }
}
