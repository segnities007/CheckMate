package com.segnities007.checkmate.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.ui.mvi.MviEffect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

internal class MainViewModel :
    BaseViewModel<MainIntent, MainState, MviEffect>(MainState()),
    KoinComponent {
    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.Navigate -> navigate(intent)
        }
    }

    private fun navigate(intent: MainIntent.Navigate) {
        viewModelScope.launch {
            sendEffect { MainEffect.Navigate(intent.route) }
        }
    }
}
