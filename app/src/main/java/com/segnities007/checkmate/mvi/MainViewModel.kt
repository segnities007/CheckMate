package com.segnities007.checkmate.mvi

import com.segnities007.navigation.NavKeys
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.user.GetUserStatusUseCase
import com.segnities007.usecase.user.IsAccountCreatedUseCase

class MainViewModel(
    private val isAccountCreatedUseCase: IsAccountCreatedUseCase,
    private val getUserStatusUseCase: GetUserStatusUseCase,
) : BaseViewModel<MainIntent, MainState, MainEffect>(MainState()) {
    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.Navigate -> navigate(intent)
            MainIntent.CheckAccount -> checkAccount()
            is MainIntent.ShowToast -> showToast(intent)
            MainIntent.LoadUserStatus -> loadUserStatus()
            MainIntent.Logout -> logout()
            MainIntent.GoBack -> goBack()
            is MainIntent.SetBottomBar -> setState { copy(bottomBar = intent.bottomBar) }
            is MainIntent.SetFab -> setState { copy(fab = intent.fab) }
            is MainIntent.SetTopBar -> setState { copy(topBar = intent.topBar) }
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
                    navigate(MainIntent.Navigate(NavKeys.Hub.HomeKey))
                } else {
                    navigate(MainIntent.Navigate(NavKeys.Auth.LoginKey))
                }
            },
            onFailure = {
                navigate(MainIntent.Navigate(NavKeys.Auth.LoginKey))
            }
        )
    }

    private fun loadUserStatus() {
        execute(
            action = { getUserStatusUseCase().getOrThrow() },
            reducer = { userStatus -> copy(userStatus = userStatus) }
        )
    }

    private fun navigate(intent: MainIntent.Navigate) {
        val currentStack = currentState.backStack
        // Prevent duplicate top navigation
        if (currentStack.lastOrNull() != intent.route) {
            setState { copy(backStack = currentStack + intent.route) }
        }
    }

    private fun goBack() {
        val currentStack = currentState.backStack
        if (currentStack.size > 1) {
            setState { copy(backStack = currentStack.dropLast(1)) }
        }
    }

    private fun showToast(intent: MainIntent.ShowToast) {
        sendEffect { MainEffect.ShowToast(intent.message) }
    }

    private fun logout() {
        // TODO: Implement logout use case if needed
        navigate(MainIntent.Navigate(NavKeys.Auth.LoginKey))
    }
}