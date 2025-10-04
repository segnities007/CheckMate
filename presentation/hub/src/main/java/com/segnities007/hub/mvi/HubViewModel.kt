package com.segnities007.hub.mvi

import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.user.GetUserStatusUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class HubViewModel(
    private val getUserStatusUseCase: GetUserStatusUseCase,
) : BaseViewModel<HubIntent, HubState, HubEffect>(HubState()),
    KoinComponent {
    private val reducer: HubReducer = HubReducer()

    override suspend fun handleIntent(intent: HubIntent) {
        when (intent) {
            is HubIntent.Navigate -> navigate(intent)
            is HubIntent.ShowToast -> showToast(intent)
            HubIntent.LoadUserStatus -> loadUserStatus()
            HubIntent.Logout -> logout()
            is HubIntent.SetBottomBar -> setBottomBar(intent)
            is HubIntent.SetFab -> setFab(intent)
            is HubIntent.SetTopBar -> setTopBar(intent)
        }
    }

    init {
        sendIntent(HubIntent.LoadUserStatus)
    }

    private suspend fun loadUserStatus() {
        getUserStatusUseCase().fold(
            onSuccess = { userStatus ->
                setState { copy(userStatus = userStatus) }
            },
            onFailure = { e ->
                // エラーハンドリング: ログ出力またはEffect発行
            }
        )
    }

    private fun setBottomBar(intent: HubIntent.SetBottomBar) {
        setState { reducer.reduce(this, intent) }
    }

    private fun setTopBar(intent: HubIntent.SetTopBar) {
        setState { reducer.reduce(this, intent) }
    }

    private fun setFab(intent: HubIntent.SetFab) {
        setState { reducer.reduce(this, intent) }
    }

    private fun navigate(intent: HubIntent.Navigate) {
        setState { reducer.reduce(this, intent) }
        sendEffect { HubEffect.Navigate(intent.hubRoute) }
    }

    private fun showToast(intent: HubIntent.ShowToast) {
        sendEffect { HubEffect.ShowToast(intent.message) }
    }

    private fun logout() {
        sendEffect { HubEffect.Logout }
    }
}
