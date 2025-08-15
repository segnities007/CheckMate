package com.segnities007.items.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.repository.ItemRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ItemsViewModel(
    private val itemRepository: ItemRepository
): BaseViewModel<ItemIntent, ItemState, ItemEffect>(
    ItemState()
), KoinComponent{

    init {
        getAllItems()
    }

    override suspend fun handleIntent(intent: ItemIntent) {
        when(intent){
            is ItemIntent.GetAllItems -> getAllItems()
            is ItemIntent.GetItemById -> getItemById(intent)
            is ItemIntent.InsertItem -> insertItem(intent)
            is ItemIntent.DeleteItem -> deleteItem(intent)
        }
    }

    private fun getAllItems(){
        viewModelScope.launch(Dispatchers.IO){
            val items = itemRepository.getAllItems()
            setState { copy(items = items) }
        }
    }

    private fun getItemById(intent: ItemIntent.GetItemById){
        viewModelScope.launch(Dispatchers.IO){
            val item = itemRepository.getItemById(intent.id)
            if(item != null) setState { copy(items = listOf(item)) }
        }
    }

    private fun insertItem(intent: ItemIntent.InsertItem){
        viewModelScope.launch(Dispatchers.IO){
            itemRepository.insertItem(intent.item)
            getAllItems()
        }
    }

    private suspend fun deleteItem(intent: ItemIntent.DeleteItem){
        viewModelScope.launch(Dispatchers.IO){
            itemRepository.deleteItem(intent.id)
            getAllItems()
        }
    }
}