package com.segnities007.items.mvi

import com.segnities007.ui.mvi.MviEffect

sealed interface ItemEffect: MviEffect {
    data class ShowToast(val message: String): ItemEffect
}