package com.segnities007.home.mvi

import com.segnities007.ui.mvi.MviReducer

class HomeReducer : MviReducer<HomeState, HomeIntent> {
    override fun reduce(currentState: HomeState, intent: HomeIntent): HomeState {
        return when (intent) {
            is HomeIntent.SetAllItems -> currentState.copy(allItem = intent.allItems)
            is HomeIntent.SetItemCheckStates -> currentState.copy(itemCheckStates = intent.itemCheckStates)
            else -> currentState
        }
    }
}
