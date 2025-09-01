package com.segnities007.items.mvi

import com.segnities007.ui.mvi.MviReducer

class ItemsReducer : MviReducer<ItemsState, ItemsIntent> {
    override fun reduce(currentState: ItemsState, intent: ItemsIntent): ItemsState {
        return when (intent) {
            is ItemsIntent.UpdateIsShowBottomSheet -> currentState.copy(isShowBottomSheet = intent.isShowBottomSheet)
            is ItemsIntent.UpdateCapturedImageUriForBottomSheet -> currentState.copy(capturedImageUriForBottomSheet = intent.capturedImageUriForBottomSheet)
            is ItemsIntent.UpdateCapturedTempPathForViewModel -> currentState.copy(capturedTempPathForViewModel = intent.capturedTempPathForViewModel)
            is ItemsIntent.UpdateSearchQuery -> currentState.copy(searchQuery = intent.query)
            is ItemsIntent.UpdateSelectedCategory -> currentState.copy(selectedCategory = intent.category)
            is ItemsIntent.UpdateSortOrder -> currentState.copy(sortOrder = intent.sortOrder)
            else -> currentState
        }
    }
}
