package com.segnities007.hub.mvi

import com.segnities007.ui.mvi.MviReducer

class HubReducer : MviReducer<HubState, HubIntent> {
    override fun reduce(
        currentState: HubState,
        intent: HubIntent,
    ): HubState =
        when (intent) {
            is HubIntent.SetBottomBar -> currentState.copy(bottomBar = intent.bottomBar)
            is HubIntent.SetTopBar -> currentState.copy(topBar = intent.topBar)
            is HubIntent.SetFab -> currentState.copy(fab = intent.fab)
            is HubIntent.Navigate -> currentState.copy(currentHubRoute = intent.hubRoute)
            else -> currentState
        }
}
