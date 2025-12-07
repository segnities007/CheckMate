package com.segnities007.navigation.mvi

import com.segnities007.common.keys.NavKeys
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.user.GetUserStatusUseCase
import com.segnities007.usecase.user.IsAccountCreatedUseCase

class MainViewModel(
    private val isAccountCreatedUseCase: IsAccountCreatedUseCase,
    private val getUserStatusUseCase: GetUserStatusUseCase,
) : BaseViewModel<MainIntent, MainState, MainEffect>(MainState()) {
    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.CheckAccount -> checkAccount()
            is MainIntent.ShowToast -> showToast(intent)
            MainIntent.LoadUserStatus -> loadUserStatus()
            MainIntent.Logout -> logout()
        }
    }

    init {
        sendIntent(MainIntent.CheckAccount)
        sendIntent(MainIntent.LoadUserStatus)
    }

    private suspend fun checkAccount() {
        isAccountCreatedUseCase().fold(
            onSuccess = { isCreated ->
                if (isCreated) {
                    sendEffect { MainEffect.Navigate(NavKeys.Hub.HomeKey) }
                } else {
                    sendEffect { MainEffect.Navigate(NavKeys.Auth.LoginKey) }
                }
            },
            onFailure = {
                sendEffect { MainEffect.Navigate(NavKeys.Auth.LoginKey) }
            }
        )
    }

    private fun loadUserStatus() {
        execute(
            action = { getUserStatusUseCase().getOrThrow() },
            reducer = { userStatus -> copy(userStatus = userStatus) }
        )
    }

    private fun showToast(intent: MainIntent.ShowToast) {
        sendEffect { MainEffect.ShowToast(intent.message) }
    }

    private fun logout() {
        // TODO: Implement logout use case if needed
        sendEffect { MainEffect.Navigate(NavKeys.Auth.LoginKey) }
    }
}