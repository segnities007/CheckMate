package com.segnities007.hub.mvi

import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.user.GetUserStatusUseCase

class HubViewModel(
    private val getUserStatusUseCase: GetUserStatusUseCase,
) : BaseViewModel<HubIntent, HubState, HubEffect>(HubState()) {

    override suspend fun handleIntent(intent: HubIntent) {
        when (intent) {
            is HubIntent.Navigate -> navigate(intent)
            is HubIntent.ShowToast -> showToast(intent)
            HubIntent.LoadUserStatus -> loadUserStatus()
            HubIntent.Logout -> logout()
            is HubIntent.SetBottomBar -> setState { copy(bottomBar = intent.bottomBar) }
            is HubIntent.SetFab -> setState { copy(fab = intent.fab) }
            is HubIntent.SetTopBar -> setState { copy(topBar = intent.topBar) }
        }
    }

    init {
        sendIntent(HubIntent.LoadUserStatus)
    }

    private fun loadUserStatus() {
        execute(
            action = { getUserStatusUseCase().getOrThrow() },
            reducer = { userStatus -> copy(userStatus = userStatus) }
        )
    }

    private fun navigate(intent: HubIntent.Navigate) {
        setState { copy(currentHubRoute = intent.hubRoute) }
    }

    private fun showToast(intent: HubIntent.ShowToast) {
        sendEffect { HubEffect.ShowToast(intent.message) }
    }

    private fun logout() {
        sendEffect { HubEffect.Logout }
    }
}
