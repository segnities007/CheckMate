package com.segnities007.home.mvi

import com.segnities007.ui.mvi.MviEffect

sealed interface HomeEffect : MviEffect {
    data class ShowError(val message: String) : HomeEffect
}
