package com.segnities007.items.mvi

import com.segnities007.ui.mvi.MviEffect

sealed interface ItemsEffect : MviEffect {
    data class ShowToast(
        val message: String,
    ) : ItemsEffect

    data object NavigateToItemsList : ItemsEffect

    data object NavigateToCameraCapture : ItemsEffect
}
