package com.segnities007.auth.mvi

import com.segnities007.navigation.NavKey
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.user.IsAccountCreatedUseCase

class AuthViewModel(
    private val isAccountCreatedUseCase: IsAccountCreatedUseCase,
) : BaseViewModel<AuthIntent, AuthState, AuthEffect>(AuthState()) {
    override suspend fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Navigate -> navigate(intent)
            is AuthIntent.ShowToast -> showToast(intent)
            AuthIntent.CheckAccount -> checkAccount()
            is AuthIntent.TopNavigate -> topNavigate(intent)
        }
    }

    init {
        sendIntent(AuthIntent.CheckAccount)
    }

    private fun init() {
        // kept for compatibility but not used; checkAccount handles the logic
    }

    private suspend fun checkAccount() {
        isAccountCreatedUseCase().fold(
            onSuccess = { isCreated ->
                if (isCreated) {
                    topNavigate(AuthIntent.TopNavigate(NavKey.Hub))
                } else {
                    navigate(AuthIntent.Navigate(NavKey.Login))
                }
            },
            onFailure = { e ->
                // エラー時はログイン画面に遷移
                navigate(AuthIntent.Navigate(NavKey.Login))
            }
        )
    }

    private fun navigate(intent: AuthIntent.Navigate) {
        setState { copy(currentRoute = intent.authRoute) }
    }

    private fun showToast(intent: AuthIntent.ShowToast) {
        sendEffect { AuthEffect.ShowToast(intent.message) }
    }

    private fun topNavigate(intent: AuthIntent.TopNavigate) {
        sendEffect { AuthEffect.TopNavigate(intent.route) }
    }
}
