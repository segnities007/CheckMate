package com.segnities007.hub.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.repository.UserRepository
import com.segnities007.ui.mvi.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class HubViewModel(
    private val userRepository: UserRepository,
) : BaseViewModel<HubIntent, HubState, HubEffect>(HubState()),
    KoinComponent {
    override suspend fun handleIntent(intent: HubIntent) {
        when (intent) {
            is HubIntent.Navigate -> navigate(intent)
            is HubIntent.ShowToast -> showToast(intent)
            HubIntent.Logout -> logout()
            HubIntent.ShowNavigationBar -> showNavigationBar()
            HubIntent.HideNavigationBar -> hideNavigationBar()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userStatus = userRepository.getUserStatus()
            setState { copy(userStatus = userStatus) }
        }
    }

    private fun showNavigationBar() {
        setState { copy(isShowNavigationBar = true) }
    }

    private fun hideNavigationBar() {
        setState { copy(isShowNavigationBar = false) }
    }

    private fun navigate(intent: HubIntent.Navigate) {
        sendEffect { HubEffect.Navigate(intent.hubRoute) }
    }

    private fun showToast(intent: HubIntent.ShowToast) {
        sendEffect { HubEffect.ShowToast(intent.message) }
    }

    private fun logout() {
        sendEffect { HubEffect.Logout }
    }
}
