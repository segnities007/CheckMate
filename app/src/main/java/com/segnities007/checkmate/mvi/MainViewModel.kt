package com.segnities007.checkmate.mvi

import com.segnities007.ui.mvi.BaseViewModel

internal class MainViewModel :
    BaseViewModel<MainIntent, MainState, MainEffect>(MainState()) {
    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.Navigate -> navigate(intent)
        }
    }

    private fun navigate(intent: MainIntent.Navigate) {
        setState { copy(currentRoute = intent.route) }
    }
}
