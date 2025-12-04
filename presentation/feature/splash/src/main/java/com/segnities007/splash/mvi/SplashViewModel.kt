package com.segnities007.splash.mvi

import com.segnities007.navigation.NavKeys
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.user.IsAccountCreatedUseCase
import kotlinx.coroutines.delay

class SplashViewModel(
    private val isAccountCreatedUseCase: IsAccountCreatedUseCase,
) : BaseViewModel<SplashIntent, SplashState, SplashEffect>(SplashState) {

    init {
        sendIntent(SplashIntent.CheckAccount)
    }

    override suspend fun handleIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.CheckAccount -> checkAccount()
        }
    }

    private suspend fun checkAccount() {
        // Add a small delay to show splash screen (optional, but good for UX)
        delay(1000)

        isAccountCreatedUseCase().fold(
            onSuccess = { isCreated ->
                if (isCreated) {
                    sendEffect { SplashEffect.NavigateTo(NavKeys.Hub.HomeKey) }
                } else {
                    sendEffect { SplashEffect.NavigateTo(NavKeys.Auth.LoginKey) }
                }
            },
            onFailure = {
                sendEffect { SplashEffect.NavigateTo(NavKeys.Auth.LoginKey) }
            }
        )
    }
}
