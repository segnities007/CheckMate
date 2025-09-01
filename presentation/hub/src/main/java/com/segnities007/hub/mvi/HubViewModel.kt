package com.segnities007.hub.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.repository.UserRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class HubViewModel(
    private val userRepository: UserRepository,
    private val reducer: HubReducer = HubReducer(),
) : BaseViewModel<HubIntent, HubState, HubEffect>(HubState()),
    KoinComponent {
    override suspend fun handleIntent(intent: HubIntent) {
        when (intent) {
            is HubIntent.Navigate -> navigate(intent)
            is HubIntent.ShowToast -> showToast(intent)
            HubIntent.Logout -> logout()
            is HubIntent.SetBottomBar -> setBottomBar(intent)
            is HubIntent.SetFab -> setFab(intent)
            is HubIntent.SetTopBar -> setTopBar(intent)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userStatus = userRepository.getUserStatus()
            setState { it.copy(userStatus = userStatus) }
        }
    }

    private fun setBottomBar(intent: HubIntent.SetBottomBar) {
        setState { state -> reducer.reduce(state, intent) }
    }

    private fun setTopBar(intent: HubIntent.SetTopBar) {
        setState { state -> reducer.reduce(state, intent) }
    }

    private fun setFab(intent: HubIntent.SetFab) {
        setState { state -> reducer.reduce(state, intent) }
    }

    private fun navigate(intent: HubIntent.Navigate) {
        setState { state -> reducer.reduce(state, intent) }
        sendEffect { HubEffect.Navigate(intent.hubRoute) }
    }

    private fun showToast(intent: HubIntent.ShowToast) {
        sendEffect { HubEffect.ShowToast(intent.message) }
    }

    private fun logout() {
        sendEffect { HubEffect.Logout }
    }
}
